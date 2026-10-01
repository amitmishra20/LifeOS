package com.lifeos.recommendation.service;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalHealth;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.dto.GoalHealthEvaluationDto;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.goal.service.GoalHealthService;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitLog;
import com.lifeos.habit.entity.HabitPauseInterval;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitLogRepository;
import com.lifeos.habit.repository.HabitPauseIntervalRepository;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.recommendation.config.FocusScoringProperties;
import com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse;
import com.lifeos.recommendation.dto.DailyFocusResponse;
import com.lifeos.recommendation.dto.FocusItemResponse;
import com.lifeos.recommendation.dto.RecommendationItemDto;
import com.lifeos.recommendation.dto.RecommendationType;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final TaskRepository taskRepository;
    private final FocusScoringProperties scoringProperties;
    private final GoalHealthService goalHealthService;
    private final GoalRepository goalRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HabitPauseIntervalRepository pauseIntervalRepository;
    private final LearningItemRepository learningItemRepository;
    private final LearningSessionRepository learningSessionRepository;

    public RecommendationServiceImpl(
            TaskRepository taskRepository,
            FocusScoringProperties scoringProperties,
            GoalHealthService goalHealthService,
            GoalRepository goalRepository,
            HabitRepository habitRepository,
            HabitLogRepository habitLogRepository,
            HabitPauseIntervalRepository pauseIntervalRepository,
            LearningItemRepository learningItemRepository,
            LearningSessionRepository learningSessionRepository
    ) {
        this.taskRepository = taskRepository;
        this.scoringProperties = scoringProperties;
        this.goalHealthService = goalHealthService;
        this.goalRepository = goalRepository;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.pauseIntervalRepository = pauseIntervalRepository;
        this.learningItemRepository = learningItemRepository;
        this.learningSessionRepository = learningSessionRepository;
    }

    public enum TransientGoalHealth {
        ON_TRACK,
        AT_RISK,
        BEHIND,
        COMPLETED,
        NONE
    }

    public static class GoalHealthAssessment {
        public final TransientGoalHealth health;
        public final int score;
        public final String reason;

        public GoalHealthAssessment(TransientGoalHealth health, int score, String reason) {
            this.health = health;
            this.score = score;
            this.reason = reason;
        }
    }

    // =========================================================================
    // 1. LOCKED DAILY FOCUS (Phase 6 implementation preserved exactly)
    // =========================================================================

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
            return new GoalHealthAssessment(TransientGoalHealth.NONE, 0, null);
        }

        GoalHealth health = goalHealthService.evaluate(goal, today);
        switch (health) {
            case COMPLETED -> {
                return new GoalHealthAssessment(TransientGoalHealth.COMPLETED, 0, null);
            }
            case ON_TRACK -> {
                return new GoalHealthAssessment(TransientGoalHealth.ON_TRACK, scoringProperties.getGoalOnTrack(), null);
            }
            case AT_RISK -> {
                String reason = (goal.getTargetDate() != null && goal.getTargetDate().isEqual(today))
                        ? "Supports goal due today"
                        : "Supports goal falling behind schedule";
                return new GoalHealthAssessment(TransientGoalHealth.AT_RISK, scoringProperties.getGoalAtRisk(), reason);
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
                return new GoalHealthAssessment(TransientGoalHealth.BEHIND, scoringProperties.getGoalBehind(), reason);
            }
            default -> {
                return new GoalHealthAssessment(TransientGoalHealth.NONE, 0, null);
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
        if (healthAssessment.health == TransientGoalHealth.BEHIND) {
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
        if (healthAssessment.health == TransientGoalHealth.AT_RISK) {
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

    // =========================================================================
    // 2. CONSOLIDATED RECOMMENDATIONS (Phase 13 Multi-domain)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public ConsolidatedRecommendationsResponse getConsolidatedRecommendations(Long userId) {
        LocalDate today = LocalDate.now();

        // 1. Daily Focus Tasks (LOCKED)
        DailyFocusResponse dailyFocus = getDailyFocus(userId);
        List<FocusItemResponse> dailyFocusTasks = dailyFocus.getItems();

        // 2. Habit Nudges (Weekly consistency < 50%)
        List<RecommendationItemDto> habitNudges = generateHabitNudges(userId, today);

        // 3. Learning Focus (Active, incomplete, no session in 7 days)
        List<RecommendationItemDto> learningFocus = generateLearningFocus(userId, today);

        // 4. Strategic Alerts (Active goals in BEHIND or AT_RISK state)
        List<RecommendationItemDto> strategicAlerts = generateStrategicAlerts(userId, today);

        ConsolidatedRecommendationsResponse response = new ConsolidatedRecommendationsResponse();
        response.setGeneratedDate(today);
        response.setDailyFocusTasks(dailyFocusTasks);
        response.setHabitNudges(habitNudges);
        response.setLearningFocus(learningFocus);
        response.setStrategicAlerts(strategicAlerts);
        response.setTotalRecommendationsCount(
                dailyFocusTasks.size() + habitNudges.size() + learningFocus.size() + strategicAlerts.size()
        );

        return response;
    }

    private List<RecommendationItemDto> generateHabitNudges(Long userId, LocalDate today) {
        List<Habit> activeHabits = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, HabitStatus.ACTIVE);
        if (activeHabits.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate windowStart = today.minusDays(6);
        List<HabitLog> logs = habitLogRepository.findByUserIdAndCompletionDateBetween(userId, windowStart, today);
        List<HabitPauseInterval> pauseIntervals = pauseIntervalRepository.findByUserIdOrderByPausedAtAsc(userId);

        Map<Long, List<HabitLog>> logsByHabit = logs.stream()
                .collect(Collectors.groupingBy(log -> log.getHabit().getId()));
        Map<Long, List<HabitPauseInterval>> pauseByHabit = pauseIntervals.stream()
                .collect(Collectors.groupingBy(interval -> interval.getHabit().getId()));

        ZoneId zoneId = ZoneId.systemDefault();
        List<HabitNudgeEvaluation> evaluatedHabits = new ArrayList<>();

        for (Habit habit : activeHabits) {
            List<HabitLog> habitLogs = logsByHabit.getOrDefault(habit.getId(), Collections.emptyList());
            List<HabitPauseInterval> habitPauses = pauseByHabit.getOrDefault(habit.getId(), Collections.emptyList());
            Set<LocalDate> completedDates = habitLogs.stream()
                    .map(HabitLog::getCompletionDate)
                    .collect(Collectors.toSet());

            LocalDate habitCreatedDate = habit.getCreatedAt() != null
                    ? habit.getCreatedAt().atZone(zoneId).toLocalDate()
                    : windowStart;
            if (habitCreatedDate.isAfter(today)) {
                habitCreatedDate = today;
            }

            int consistencyRate = calculateRolling7DayConsistency(habit, completedDates, habitPauses, habitCreatedDate, today, windowStart, zoneId);

            if (consistencyRate < 50) {
                String subtitle = switch (habit.getFrequencyType()) {
                    case DAILY -> "Daily habit";
                    case SPECIFIC_DAYS -> "Scheduled days habit";
                    case WEEKLY_TARGET -> "Weekly target (" + habit.getTargetPerWeek() + "x/week)";
                };

                String primaryReason = "Weekly consistency is " + consistencyRate + "% (below 50% target threshold).";
                List<String> reasons = new ArrayList<>();
                reasons.add(primaryReason);
                if (habit.getGoal() != null) {
                    reasons.add("Supports goal: " + habit.getGoal().getTitle());
                }

                RecommendationItemDto dto = new RecommendationItemDto(
                        "HABIT-" + habit.getId(),
                        RecommendationType.HABIT_NUDGE,
                        habit.getId(),
                        habit.getTitle(),
                        subtitle,
                        primaryReason,
                        reasons,
                        "/habits"
                );
                evaluatedHabits.add(new HabitNudgeEvaluation(dto, consistencyRate, habit.getTargetPerWeek() != null ? habit.getTargetPerWeek() : 1, habit.getId()));
            }
        }

        // Sort: consistencyRate ASC, then targetPerWeek DESC, then ID ASC. Limit: 3
        return evaluatedHabits.stream()
                .sorted(Comparator.comparingInt(HabitNudgeEvaluation::getConsistencyRate)
                        .thenComparing(Comparator.comparingInt(HabitNudgeEvaluation::getTargetPerWeek).reversed())
                        .thenComparing(HabitNudgeEvaluation::getHabitId))
                .map(HabitNudgeEvaluation::getDto)
                .limit(3)
                .collect(Collectors.toList());
    }

    private int calculateRolling7DayConsistency(
            Habit habit,
            Set<LocalDate> completedDates,
            List<HabitPauseInterval> pauseIntervals,
            LocalDate habitCreatedDate,
            LocalDate today,
            LocalDate windowStart,
            ZoneId zoneId
    ) {
        HabitFrequencyType freq = habit.getFrequencyType();

        if (freq == HabitFrequencyType.DAILY) {
            int denominator = 0;
            int numerator = 0;
            LocalDate d = windowStart;
            while (!d.isAfter(today)) {
                if (!d.isBefore(habitCreatedDate) && !isDatePaused(d, pauseIntervals, zoneId)) {
                    denominator++;
                    if (completedDates.contains(d)) {
                        numerator++;
                    }
                }
                d = d.plusDays(1);
            }
            return denominator == 0 ? 100 : (int) Math.round(((double) numerator / denominator) * 100.0);

        } else if (freq == HabitFrequencyType.SPECIFIC_DAYS) {
            Set<Integer> targetDays = parseDaysMask(habit.getTargetDaysMask());
            int denominator = 0;
            int numerator = 0;
            LocalDate d = windowStart;
            while (!d.isAfter(today)) {
                if (!d.isBefore(habitCreatedDate) && targetDays.contains(d.getDayOfWeek().getValue()) && !isDatePaused(d, pauseIntervals, zoneId)) {
                    denominator++;
                    if (completedDates.contains(d)) {
                        numerator++;
                    }
                }
                d = d.plusDays(1);
            }
            return denominator == 0 ? 100 : (int) Math.round(((double) numerator / denominator) * 100.0);

        } else if (freq == HabitFrequencyType.WEEKLY_TARGET) {
            int target = habit.getTargetPerWeek() != null && habit.getTargetPerWeek() > 0 ? habit.getTargetPerWeek() : 1;
            int numerator = 0;
            LocalDate d = windowStart;
            while (!d.isAfter(today)) {
                if (!d.isBefore(habitCreatedDate) && !isDatePaused(d, pauseIntervals, zoneId)) {
                    if (completedDates.contains(d)) {
                        numerator++;
                    }
                }
                d = d.plusDays(1);
            }
            return Math.min(100, (int) Math.round(((double) numerator / target) * 100.0));
        }

        return 100;
    }

    private boolean isDatePaused(LocalDate date, List<HabitPauseInterval> pauseIntervals, ZoneId zoneId) {
        for (HabitPauseInterval interval : pauseIntervals) {
            LocalDate pauseStart = interval.getPausedAt().atZone(zoneId).toLocalDate();
            if (interval.getResumedAt() != null) {
                LocalDate resumeDate = interval.getResumedAt().atZone(zoneId).toLocalDate();
                if (!date.isBefore(pauseStart) && date.isBefore(resumeDate)) {
                    return true;
                }
            } else {
                if (!date.isBefore(pauseStart)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<Integer> parseDaysMask(String mask) {
        if (mask == null || mask.trim().isEmpty()) {
            return Collections.emptySet();
        }
        Set<Integer> days = new HashSet<>();
        for (String part : mask.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                try {
                    days.add(Integer.parseInt(trimmed));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return days;
    }

    private List<RecommendationItemDto> generateLearningFocus(Long userId, LocalDate today) {
        List<LearningItem> activeItems = learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, LearningStatus.ACTIVE);
        if (activeItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<LearningItem> incompleteItems = activeItems.stream()
                .filter(item -> item.getCurrentProgress() == null || item.getTargetProgress() == null || item.getCurrentProgress() < item.getTargetProgress())
                .collect(Collectors.toList());

        if (incompleteItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<LearningSession> sessions = learningSessionRepository.findByUserIdOrderBySessionDateDescCreatedAtDesc(userId);
        Map<Long, List<LearningSession>> sessionsByItem = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getLearningItem().getId()));

        LocalDate sevenDaysAgo = today.minusDays(7);
        List<LearningFocusEvaluation> evaluatedItems = new ArrayList<>();

        for (LearningItem item : incompleteItems) {
            List<LearningSession> itemSessions = sessionsByItem.getOrDefault(item.getId(), Collections.emptyList());
            LocalDate lastSessionDate = itemSessions.isEmpty() ? null : itemSessions.get(0).getSessionDate();

            boolean isStalled = (lastSessionDate == null) || lastSessionDate.isBefore(sevenDaysAgo.plusDays(1)); // <= 7 days ago

            if (isStalled) {
                long daysInactive;
                if (lastSessionDate != null) {
                    daysInactive = ChronoUnit.DAYS.between(lastSessionDate, today);
                } else {
                    daysInactive = item.getCreatedAt() != null
                            ? Math.max(7, ChronoUnit.DAYS.between(item.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate(), today))
                            : 7;
                }

                int currentProgress = item.getCurrentProgress() != null ? item.getCurrentProgress() : 0;
                int targetProgress = item.getTargetProgress() != null ? item.getTargetProgress() : 100;
                int progressDeficit = Math.max(0, targetProgress - currentProgress);

                String primaryReason = lastSessionDate != null
                        ? "No learning sessions logged in the last " + daysInactive + " days; progress is at " + currentProgress + "% of " + targetProgress + "% target."
                        : "No learning sessions recorded yet; progress is at " + currentProgress + "% of " + targetProgress + "% target.";

                List<String> reasons = new ArrayList<>();
                reasons.add(primaryReason);
                if (item.getGoal() != null) {
                    reasons.add("Connected to goal: " + item.getGoal().getTitle());
                }

                String subtitle = item.getCategory() != null ? item.getCategory().name() + " Subject" : "Learning Subject";

                RecommendationItemDto dto = new RecommendationItemDto(
                        "LEARN-" + item.getId(),
                        RecommendationType.LEARNING_FOCUS,
                        item.getId(),
                        item.getTitle(),
                        subtitle,
                        primaryReason,
                        reasons,
                        "/learning"
                );

                evaluatedItems.add(new LearningFocusEvaluation(dto, daysInactive, progressDeficit, item.getId()));
            }
        }

        // Sort: daysInactive DESC, then progressDeficit DESC, then ID ASC. Limit: 3
        return evaluatedItems.stream()
                .sorted(Comparator.comparingLong(LearningFocusEvaluation::getDaysInactive).reversed()
                        .thenComparing(Comparator.comparingInt(LearningFocusEvaluation::getProgressDeficit).reversed())
                        .thenComparing(LearningFocusEvaluation::getItemId))
                .map(LearningFocusEvaluation::getDto)
                .limit(3)
                .collect(Collectors.toList());
    }

    private List<RecommendationItemDto> generateStrategicAlerts(Long userId, LocalDate today) {
        List<Goal> activeGoals = goalRepository.findByUserIdAndStatus(userId, GoalStatus.ACTIVE);
        if (activeGoals.isEmpty()) {
            return Collections.emptyList();
        }

        List<StrategicAlertEvaluation> evaluatedGoals = new ArrayList<>();

        for (Goal goal : activeGoals) {
            GoalHealthEvaluationDto healthEval = goalHealthService.evaluateWithDetails(goal, today);

            if (healthEval.getHealth() == GoalHealth.BEHIND || healthEval.getHealth() == GoalHealth.AT_RISK) {
                String subtitle = "Goal Health: " + (healthEval.getHealth() == GoalHealth.BEHIND ? "Behind Schedule" : "At Risk");
                String primaryReason = healthEval.getReason();

                List<String> reasons = new ArrayList<>();
                reasons.add(primaryReason);
                reasons.add("Actual Progress: " + healthEval.getActualProgress() + "% (Expected: " + healthEval.getExpectedProgress() + "%, Delta: " + healthEval.getDelta() + "%)");
                if (goal.getTargetDate() != null) {
                    reasons.add("Target Date: " + goal.getTargetDate());
                }

                RecommendationItemDto dto = new RecommendationItemDto(
                        "GOAL-" + goal.getId(),
                        RecommendationType.STRATEGIC_ALERT,
                        goal.getId(),
                        goal.getTitle(),
                        subtitle,
                        primaryReason,
                        reasons,
                        "/goals/" + goal.getId()
                );

                int severityRank = healthEval.getHealth() == GoalHealth.BEHIND ? 0 : 1;
                evaluatedGoals.add(new StrategicAlertEvaluation(dto, severityRank, goal.getTargetDate(), goal.getId()));
            }
        }

        // Sort: severityRank ASC (BEHIND=0, AT_RISK=1), targetDate ASC (nulls last), ID ASC. Limit: 4
        return evaluatedGoals.stream()
                .sorted(Comparator.comparingInt(StrategicAlertEvaluation::getSeverityRank)
                        .thenComparing(StrategicAlertEvaluation::getTargetDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StrategicAlertEvaluation::getGoalId))
                .map(StrategicAlertEvaluation::getDto)
                .limit(4)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Helper Classes for Sorting
    // =========================================================================

    private static class HabitNudgeEvaluation {
        private final RecommendationItemDto dto;
        private final int consistencyRate;
        private final int targetPerWeek;
        private final Long habitId;

        public HabitNudgeEvaluation(RecommendationItemDto dto, int consistencyRate, int targetPerWeek, Long habitId) {
            this.dto = dto;
            this.consistencyRate = consistencyRate;
            this.targetPerWeek = targetPerWeek;
            this.habitId = habitId;
        }

        public RecommendationItemDto getDto() { return dto; }
        public int getConsistencyRate() { return consistencyRate; }
        public int getTargetPerWeek() { return targetPerWeek; }
        public Long getHabitId() { return habitId; }
    }

    private static class LearningFocusEvaluation {
        private final RecommendationItemDto dto;
        private final long daysInactive;
        private final int progressDeficit;
        private final Long itemId;

        public LearningFocusEvaluation(RecommendationItemDto dto, long daysInactive, int progressDeficit, Long itemId) {
            this.dto = dto;
            this.daysInactive = daysInactive;
            this.progressDeficit = progressDeficit;
            this.itemId = itemId;
        }

        public RecommendationItemDto getDto() { return dto; }
        public long getDaysInactive() { return daysInactive; }
        public int getProgressDeficit() { return progressDeficit; }
        public Long getItemId() { return itemId; }
    }

    private static class StrategicAlertEvaluation {
        private final RecommendationItemDto dto;
        private final int severityRank;
        private final LocalDate targetDate;
        private final Long goalId;

        public StrategicAlertEvaluation(RecommendationItemDto dto, int severityRank, LocalDate targetDate, Long goalId) {
            this.dto = dto;
            this.severityRank = severityRank;
            this.targetDate = targetDate;
            this.goalId = goalId;
        }

        public RecommendationItemDto getDto() { return dto; }
        public int getSeverityRank() { return severityRank; }
        public LocalDate getTargetDate() { return targetDate; }
        public Long getGoalId() { return goalId; }
    }
}
