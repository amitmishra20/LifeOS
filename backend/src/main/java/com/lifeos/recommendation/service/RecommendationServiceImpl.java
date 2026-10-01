package com.lifeos.recommendation.service;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.service.GoalHealthService;
import com.lifeos.recommendation.config.FocusScoringProperties;
import com.lifeos.recommendation.dto.DailyFocusResponse;
import com.lifeos.recommendation.dto.FocusItemResponse;
import com.lifeos.task.dto.TaskResponse;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskPriority;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final TaskRepository taskRepository;
    private final FocusScoringProperties scoringProperties;
    private final GoalHealthService goalHealthService;

    public RecommendationServiceImpl(
            TaskRepository taskRepository,
            FocusScoringProperties scoringProperties,
            GoalHealthService goalHealthService
    ) {
        this.taskRepository = taskRepository;
        this.scoringProperties = scoringProperties;
        this.goalHealthService = goalHealthService;
    }

    public enum GoalHealth {
        ON_TRACK,
        AT_RISK,
        BEHIND,
        COMPLETED,
        NONE
    }

    public static class GoalHealthAssessment {
        public final GoalHealth health;
        public final int score;
        public final String reason;

        public GoalHealthAssessment(GoalHealth health, int score, String reason) {
            this.health = health;
            this.score = score;
            this.reason = reason;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DailyFocusResponse getDailyFocus(Long userId) {
        LocalDate today = LocalDate.now();
        List<Task> allUserTasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Task> incompleteTasks = allUserTasks.stream()
                .filter(t -> t.getEffectiveStatus() != TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        List<FocusItemResponse> scoredItems = incompleteTasks.stream()
                .map(task -> scoreTask(task, today))
                .sorted(getTaskComparator(today))
                .limit(scoringProperties.getMaxRecommendations())
                .collect(Collectors.toList());

        DailyFocusResponse response = new DailyFocusResponse();
        response.setDate(today);
        response.setItems(scoredItems);

        if (scoredItems.isEmpty()) {
            boolean hasCompletedToday = allUserTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.COMPLETED && t.getCompletedAt() != null)
                    .anyMatch(t -> t.getCompletedAt().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(today));
            response.setAllCompleted(hasCompletedToday);
        } else {
            response.setAllCompleted(false);
        }

        return response;
    }

    private FocusItemResponse scoreTask(Task task, LocalDate today) {
        FocusItemResponse item = new FocusItemResponse();
        item.setTask(TaskResponse.fromEntity(task));

        List<String> reasons = new ArrayList<>();

        // 1. Priority scoring
        int priorityScore = getPriorityScore(task.getPriority());
        item.setPriorityScore(priorityScore);
        if (task.getPriority() == TaskPriority.CRITICAL) {
            reasons.add("Critical priority task");
        } else if (task.getPriority() == TaskPriority.HIGH) {
            reasons.add("High priority task");
        }

        // 2. Deadline & Overdue scoring
        int deadlineScore = 0;
        boolean isOverdue = task.getEffectiveStatus() == TaskStatus.OVERDUE ||
                (task.getDueDate() != null && task.getDueDate().isBefore(today));

        if (isOverdue) {
            deadlineScore = scoringProperties.getOverdue();
            reasons.add("Overdue task requires immediate action");
        } else if (task.getDueDate() != null) {
            long daysUntil = ChronoUnit.DAYS.between(today, task.getDueDate());
            if (daysUntil == 0) {
                deadlineScore = scoringProperties.getDueToday();
                reasons.add("Due today");
            } else if (daysUntil == 1) {
                deadlineScore = scoringProperties.getDueTomorrow();
                reasons.add("Due tomorrow");
            } else if (daysUntil == 2) {
                deadlineScore = scoringProperties.getDueIn2Days();
                reasons.add("Due in 2 days");
            } else if (daysUntil >= 3 && daysUntil <= 7) {
                deadlineScore = scoringProperties.getDueIn3To7Days();
                reasons.add("Due in " + daysUntil + " days");
            } else {
                deadlineScore = scoringProperties.getDueBeyond7Days();
            }
        }
        item.setDeadlineScore(deadlineScore);

        // 3. Goal Health transient scoring
        GoalHealthAssessment healthAssessment = assessGoalHealth(task.getGoal(), today);
        item.setGoalHealthScore(healthAssessment.score);
        if (healthAssessment.reason != null) {
            reasons.add(healthAssessment.reason);
        }

        // Total Score
        int totalScore = priorityScore + deadlineScore + healthAssessment.score;
        item.setTotalScore(totalScore);

        // Primary Reason determination
        String primaryReason = determinePrimaryReason(task, isOverdue, healthAssessment, today);
        item.setPrimaryReason(primaryReason);
        if (reasons.isEmpty()) {
            reasons.add(primaryReason);
        }
        item.setReasons(reasons);

        return item;
    }

    private int getPriorityScore(TaskPriority priority) {
        if (priority == null) return scoringProperties.getPriorityMedium();
        return switch (priority) {
            case CRITICAL -> scoringProperties.getPriorityCritical();
            case HIGH -> scoringProperties.getPriorityHigh();
            case MEDIUM -> scoringProperties.getPriorityMedium();
            case LOW -> scoringProperties.getPriorityLow();
        };
    }

    public GoalHealthAssessment assessGoalHealth(Goal goal, LocalDate today) {
        if (goal == null) {
            return new GoalHealthAssessment(GoalHealth.NONE, 0, null);
        }

        com.lifeos.goal.entity.GoalHealth health = goalHealthService.evaluate(goal, today);
        switch (health) {
            case COMPLETED -> {
                return new GoalHealthAssessment(GoalHealth.COMPLETED, 0, null);
            }
            case ON_TRACK -> {
                return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
            }
            case AT_RISK -> {
                String reason = (goal.getTargetDate() != null && goal.getTargetDate().isEqual(today))
                        ? "Supports goal due today"
                        : "Supports goal falling behind schedule";
                return new GoalHealthAssessment(GoalHealth.AT_RISK, scoringProperties.getGoalAtRisk(), reason);
            }
            case BEHIND -> {
                String reason;
                if (goal.getTargetDate() != null && goal.getTargetDate().isBefore(today)) {
                    reason = "Advances goal past target date";
                } else if (goal.getTargetDate() != null && goal.getTargetDate().isEqual(today)) {
                    reason = "Advances critical goal due today";
                } else {
                    reason = "Advances critical goal behind schedule";
                }
                return new GoalHealthAssessment(GoalHealth.BEHIND, scoringProperties.getGoalBehind(), reason);
            }
            default -> {
                return new GoalHealthAssessment(GoalHealth.NONE, 0, null);
            }
        }
    }

    private String determinePrimaryReason(Task task, boolean isOverdue, GoalHealthAssessment healthAssessment, LocalDate today) {
        if (isOverdue) {
            return "Overdue task requires immediate action";
        }
        if (task.getDueDate() != null && task.getDueDate().isEqual(today)) {
            return "Due today";
        }
        if (healthAssessment.health == GoalHealth.BEHIND) {
            return "Advances critical goal behind schedule";
        }
        if (task.getDueDate() != null && task.getDueDate().isEqual(today.plusDays(1))) {
            return "Due tomorrow";
        }
        if (task.getPriority() == TaskPriority.CRITICAL) {
            return "Critical priority task";
        }
        if (task.getPriority() == TaskPriority.HIGH) {
            return "High priority task";
        }
        if (healthAssessment.health == GoalHealth.AT_RISK) {
            return healthAssessment.reason != null ? healthAssessment.reason : "Supports goal falling behind schedule";
        }
        if (task.getDueDate() != null && task.getDueDate().isEqual(today.plusDays(2))) {
            return "Due in 2 days";
        }
        if (task.getPriority() == TaskPriority.MEDIUM) {
            return "Medium priority task";
        }
        if (task.getDueDate() != null) {
            long days = ChronoUnit.DAYS.between(today, task.getDueDate());
            return "Due in " + days + " days";
        }
        return "Active task";
    }

    private Comparator<FocusItemResponse> getTaskComparator(LocalDate today) {
        return (item1, item2) -> {
            // 1. Total score descending
            int scoreCompare = Integer.compare(item2.getTotalScore(), item1.getTotalScore());
            if (scoreCompare != 0) return scoreCompare;

            // 2. Overdue status (true first)
            boolean overdue1 = item1.getTask().getStatus() == TaskStatus.OVERDUE ||
                    (item1.getTask().getDueDate() != null && item1.getTask().getDueDate().isBefore(today));
            boolean overdue2 = item2.getTask().getStatus() == TaskStatus.OVERDUE ||
                    (item2.getTask().getDueDate() != null && item2.getTask().getDueDate().isBefore(today));
            if (overdue1 && !overdue2) return -1;
            if (!overdue1 && overdue2) return 1;

            // 3. Priority enum ordinal ascending (CRITICAL=0, HIGH=1, MEDIUM=2, LOW=3)
            int p1 = item1.getTask().getPriority() != null ? item1.getTask().getPriority().ordinal() : 2;
            int p2 = item2.getTask().getPriority() != null ? item2.getTask().getPriority().ordinal() : 2;
            int priorityCompare = Integer.compare(p1, p2);
            if (priorityCompare != 0) return priorityCompare;

            // 4. Due date ascending (nulls last)
            LocalDate d1 = item1.getTask().getDueDate();
            LocalDate d2 = item2.getTask().getDueDate();
            if (d1 != null && d2 != null) {
                int dateCompare = d1.compareTo(d2);
                if (dateCompare != 0) return dateCompare;
            } else if (d1 != null) {
                return -1;
            } else if (d2 != null) {
                return 1;
            }

            // 5. Creation date ascending (FIFO)
            if (item1.getTask().getCreatedAt() != null && item2.getTask().getCreatedAt() != null) {
                return item1.getTask().getCreatedAt().compareTo(item2.getTask().getCreatedAt());
            }

            // 6. ID ascending
            Long id1 = item1.getTask().getId() != null ? item1.getTask().getId() : 0L;
            Long id2 = item2.getTask().getId() != null ? item2.getTask().getId() : 0L;
            return Long.compare(id1, id2);
        };
    }
}
