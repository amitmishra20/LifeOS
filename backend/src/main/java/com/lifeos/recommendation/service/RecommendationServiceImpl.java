package com.lifeos.recommendation.service;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalStatus;
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

    public RecommendationServiceImpl(
            TaskRepository taskRepository,
            FocusScoringProperties scoringProperties
    ) {
        this.taskRepository = taskRepository;
        this.scoringProperties = scoringProperties;
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

        if (goal.getStatus() == GoalStatus.COMPLETED || (goal.getProgress() != null && goal.getProgress() >= 100)) {
            return new GoalHealthAssessment(GoalHealth.COMPLETED, 0, null);
        }

        LocalDate startDate = goal.getStartDate();
        if (startDate == null) {
            if (goal.getCreatedAt() != null) {
                startDate = goal.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                startDate = today;
            }
        }

        LocalDate targetDate = goal.getTargetDate();
        if (targetDate == null) {
            return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
        }

        if (targetDate.isBefore(startDate)) {
            return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
        }

        if (targetDate.isBefore(today)) {
            return new GoalHealthAssessment(
                    GoalHealth.BEHIND,
                    scoringProperties.getGoalBehind(),
                    "Advances goal past target date"
            );
        }

        int currentProgress = goal.getProgress() != null ? goal.getProgress() : 0;

        if (startDate.isEqual(targetDate)) {
            if (today.isBefore(targetDate)) {
                return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
            } else if (today.isEqual(targetDate)) {
                if (currentProgress >= 95) {
                    return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
                } else if (currentProgress >= 50) {
                    return new GoalHealthAssessment(
                            GoalHealth.AT_RISK,
                            scoringProperties.getGoalAtRisk(),
                            "Supports goal due today"
                    );
                } else {
                    return new GoalHealthAssessment(
                            GoalHealth.BEHIND,
                            scoringProperties.getGoalBehind(),
                            "Advances critical goal due today"
                    );
                }
            } else {
                return new GoalHealthAssessment(
                        GoalHealth.BEHIND,
                        scoringProperties.getGoalBehind(),
                        "Advances goal past target date"
                );
            }
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, targetDate);
        if (totalDays <= 0) totalDays = 1;

        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);
        if (elapsedDays < 0) elapsedDays = 0;
        if (elapsedDays > totalDays) elapsedDays = totalDays;

        double expectedProgress = ((double) elapsedDays / (double) totalDays) * 100.0;
        double delta = (double) currentProgress - expectedProgress;

        if (delta >= -5.0) {
            return new GoalHealthAssessment(GoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
        } else if (delta >= -25.0) {
            return new GoalHealthAssessment(
                    GoalHealth.AT_RISK,
                    scoringProperties.getGoalAtRisk(),
                    "Supports goal falling behind schedule"
            );
        } else {
            return new GoalHealthAssessment(
                    GoalHealth.BEHIND,
                    scoringProperties.getGoalBehind(),
                    "Advances critical goal behind schedule"
            );
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
            return "Supports goal falling behind schedule";
        }
        if (task.getDueDate() != null) {
            long days = ChronoUnit.DAYS.between(today, task.getDueDate());
            if (days <= 7) {
                return "Due in " + days + " days";
            }
        }
        return "Recommended for steady progress";
    }

    private Comparator<FocusItemResponse> getTaskComparator(LocalDate today) {
        return (a, b) -> {
            // 1. Total score DESC
            int scoreCmp = Integer.compare(b.getTotalScore(), a.getTotalScore());
            if (scoreCmp != 0) return scoreCmp;

            // 2. Due date ASC, nulls last
            LocalDate dateA = a.getTask().getDueDate();
            LocalDate dateB = b.getTask().getDueDate();
            if (dateA != null && dateB != null) {
                int dateCmp = dateA.compareTo(dateB);
                if (dateCmp != 0) return dateCmp;
            } else if (dateA != null) {
                return -1;
            } else if (dateB != null) {
                return 1;
            }

            // 3. Priority DESC (CRITICAL > HIGH > MEDIUM > LOW)
            int priorityA = a.getTask().getPriority() != null ? a.getTask().getPriority().ordinal() : 0;
            int priorityB = b.getTask().getPriority() != null ? b.getTask().getPriority().ordinal() : 0;
            int priorityCmp = Integer.compare(priorityB, priorityA);
            if (priorityCmp != 0) return priorityCmp;

            // 4. CreatedAt ASC
            if (a.getTask().getCreatedAt() != null && b.getTask().getCreatedAt() != null) {
                return a.getTask().getCreatedAt().compareTo(b.getTask().getCreatedAt());
            }

            return 0;
        };
    }
}
