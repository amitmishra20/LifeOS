package com.lifeos.analytics.service;

import com.lifeos.analytics.config.AnalyticsProperties;
import com.lifeos.analytics.dto.AnalyticsDashboardResponse;
import com.lifeos.analytics.dto.GoalAnalyticsResponse;
import com.lifeos.analytics.dto.HabitAnalyticsResponse;
import com.lifeos.analytics.dto.LearningAnalyticsResponse;
import com.lifeos.analytics.dto.ProductivityComponentDto;
import com.lifeos.analytics.dto.ProductivityScoreResponse;
import com.lifeos.analytics.dto.ProductivityWeightsDto;
import com.lifeos.analytics.dto.TaskAnalyticsResponse;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.habit.dto.HabitResponse;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.habit.service.HabitService;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.milestone.entity.MilestoneStatus;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitService habitService;
    private final LearningItemRepository learningItemRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final AnalyticsProperties analyticsProperties;

    public AnalyticsServiceImpl(
            GoalRepository goalRepository,
            MilestoneRepository milestoneRepository,
            TaskRepository taskRepository,
            HabitRepository habitRepository,
            HabitService habitService,
            LearningItemRepository learningItemRepository,
            LearningSessionRepository learningSessionRepository,
            AnalyticsProperties analyticsProperties
    ) {
        this.goalRepository = goalRepository;
        this.milestoneRepository = milestoneRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.habitService = habitService;
        this.learningItemRepository = learningItemRepository;
        this.learningSessionRepository = learningSessionRepository;
        this.analyticsProperties = analyticsProperties;
    }

    @Override
    public AnalyticsDashboardResponse getDashboardAnalytics(Long userId, Integer days) {
        ProductivityScoreResponse productivity = getProductivityScore(userId);
        GoalAnalyticsResponse goals = getGoalAnalytics(userId);
        TaskAnalyticsResponse tasks = getTaskAnalytics(userId, days);
        HabitAnalyticsResponse habits = getHabitAnalytics(userId);
        LearningAnalyticsResponse learning = getLearningAnalytics(userId);

        return new AnalyticsDashboardResponse(productivity, goals, tasks, habits, learning);
    }

    @Override
    public ProductivityScoreResponse getProductivityScore(Long userId) {
        GoalAnalyticsResponse goalAnalytics = getGoalAnalytics(userId);
        TaskAnalyticsResponse taskAnalytics = getTaskAnalytics(userId, null);
        HabitAnalyticsResponse habitAnalytics = getHabitAnalytics(userId);
        LearningAnalyticsResponse learningAnalytics = getLearningAnalytics(userId);

        double taskWeight = analyticsProperties.getTaskCompletion();
        double habitWeight = analyticsProperties.getHabitConsistency();
        double goalWeight = analyticsProperties.getGoalProgress();
        double learningWeight = analyticsProperties.getLearningActivity();

        boolean taskActive = taskAnalytics.getTotalTasks() > 0;
        boolean habitActive = habitAnalytics.getActiveHabitsCount() > 0;
        boolean goalActive = goalAnalytics.getTotalGoals() > 0 && goalAnalytics.getTotalMilestones() > 0;
        boolean learningActive = (learningAnalytics.getActiveItemsCount() + learningAnalytics.getCompletedItemsCount()) > 0;

        double taskScore = taskAnalytics.getCompletionRate();
        double habitScore = habitAnalytics.getConsistencyRate();
        double goalScore = goalAnalytics.getProgressPercentage();
        double learningScore = learningAnalytics.getActivityScore();

        double taskContribution = taskActive ? roundOneDecimal(taskWeight * taskScore) : 0.0;
        double habitContribution = habitActive ? roundOneDecimal(habitWeight * habitScore) : 0.0;
        double goalContribution = goalActive ? roundOneDecimal(goalWeight * goalScore) : 0.0;
        double learningContribution = learningActive ? roundOneDecimal(learningWeight * learningScore) : 0.0;

        double rawTotal = 0.0;
        if (taskActive) rawTotal += (taskWeight * taskScore);
        if (habitActive) rawTotal += (habitWeight * habitScore);
        if (goalActive) rawTotal += (goalWeight * goalScore);
        if (learningActive) rawTotal += (learningWeight * learningScore);

        double totalScore = roundOneDecimal(Math.min(100.0, rawTotal));

        ProductivityWeightsDto weightsDto = new ProductivityWeightsDto(
                taskWeight, habitWeight, goalWeight, learningWeight
        );

        Map<String, ProductivityComponentDto> components = new LinkedHashMap<>();
        components.put("taskCompletion", new ProductivityComponentDto(taskScore, taskWeight, taskContribution, taskActive));
        components.put("habitConsistency", new ProductivityComponentDto(habitScore, habitWeight, habitContribution, habitActive));
        components.put("goalProgress", new ProductivityComponentDto(goalScore, goalWeight, goalContribution, goalActive));
        components.put("learningActivity", new ProductivityComponentDto(learningScore, learningWeight, learningContribution, learningActive));

        int activeDomainCount = (taskActive ? 1 : 0) + (habitActive ? 1 : 0) + (goalActive ? 1 : 0) + (learningActive ? 1 : 0);

        return new ProductivityScoreResponse(totalScore, weightsDto, components, activeDomainCount, 4);
    }

    @Override
    public GoalAnalyticsResponse getGoalAnalytics(Long userId) {
        List<Goal> allGoals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Goal> eligibleGoals = allGoals.stream()
                .filter(g -> g.getStatus() == GoalStatus.ACTIVE || g.getStatus() == GoalStatus.COMPLETED)
                .toList();

        long activeGoals = eligibleGoals.stream().filter(g -> g.getStatus() == GoalStatus.ACTIVE).count();
        long completedGoals = eligibleGoals.stream().filter(g -> g.getStatus() == GoalStatus.COMPLETED).count();

        long totalMilestones = 0;
        long completedMilestones = 0;

        for (Goal goal : eligibleGoals) {
            totalMilestones += milestoneRepository.countByGoalIdAndUserId(goal.getId(), userId);
            completedMilestones += milestoneRepository.countByGoalIdAndUserIdAndStatus(
                    goal.getId(), userId, MilestoneStatus.COMPLETED
            );
        }

        double progressPercentage = 0.0;
        if (totalMilestones > 0) {
            progressPercentage = roundOneDecimal(((double) completedMilestones / totalMilestones) * 100.0);
        }

        return new GoalAnalyticsResponse(
                progressPercentage,
                eligibleGoals.size(),
                activeGoals,
                completedGoals,
                totalMilestones,
                completedMilestones
        );
    }

    @Override
    public TaskAnalyticsResponse getTaskAnalytics(Long userId, Integer days) {
        List<Task> allTasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Task> eligibleTasks = allTasks.stream()
                .filter(t -> {
                    if (days == null || days <= 0) return true;
                    LocalDate threshold = LocalDate.now().minusDays(days - 1);
                    LocalDate taskDate = t.getDueDate() != null
                            ? t.getDueDate()
                            : (t.getCreatedAt() != null
                            ? t.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            : LocalDate.now());
                    return !taskDate.isBefore(threshold);
                })
                .toList();

        long totalTasks = eligibleTasks.size();
        long completedTasks = eligibleTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long todoTasks = eligibleTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgressTasks = eligibleTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long overdueTasks = eligibleTasks.stream().filter(t -> t.getStatus() == TaskStatus.OVERDUE).count();

        double completionRate = 0.0;
        if (totalTasks > 0) {
            completionRate = roundOneDecimal(((double) completedTasks / totalTasks) * 100.0);
        }

        return new TaskAnalyticsResponse(
                completionRate,
                totalTasks,
                completedTasks,
                todoTasks,
                inProgressTasks,
                overdueTasks
        );
    }

    @Override
    public HabitAnalyticsResponse getHabitAnalytics(Long userId) {
        List<HabitResponse> activeHabits = habitService.getHabits(userId, HabitStatus.ACTIVE);
        long totalHabits = habitRepository.findByUserIdOrderByCreatedAtDesc(userId).size();

        double consistencyRate = 0.0;
        if (!activeHabits.isEmpty()) {
            double avg = activeHabits.stream()
                    .mapToInt(HabitResponse::getConsistencyRate)
                    .average()
                    .orElse(0.0);
            consistencyRate = roundOneDecimal(avg);
        }

        return new HabitAnalyticsResponse(consistencyRate, activeHabits.size(), totalHabits);
    }

    @Override
    public LearningAnalyticsResponse getLearningAnalytics(Long userId) {
        List<LearningItem> allItems = learningItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<LearningItem> eligibleItems = allItems.stream()
                .filter(item -> item.getStatus() != LearningStatus.ARCHIVED)
                .toList();

        long activeItems = eligibleItems.stream().filter(i -> i.getStatus() == LearningStatus.ACTIVE).count();
        long completedItems = eligibleItems.stream().filter(i -> i.getStatus() == LearningStatus.COMPLETED).count();

        double activityScore = 0.0;
        if (!eligibleItems.isEmpty()) {
            double sum = 0.0;
            for (LearningItem item : eligibleItems) {
                int target = item.getTargetProgress() != null && item.getTargetProgress() > 0
                        ? item.getTargetProgress()
                        : 100;
                int current = item.getCurrentProgress() != null ? item.getCurrentProgress() : 0;
                double p_i = Math.min(100.0, ((double) current / target) * 100.0);

                long sessionCount = learningSessionRepository.countByLearningItemIdAndUserId(item.getId(), userId);

                double s_i;
                if (sessionCount >= 1 || item.getStatus() == LearningStatus.COMPLETED) {
                    s_i = 1.0;
                } else if (p_i > 0) {
                    s_i = 0.8;
                } else {
                    s_i = 0.0;
                }

                sum += (p_i * s_i);
            }
            activityScore = roundOneDecimal(sum / eligibleItems.size());
        }

        long totalMinutes = Optional.ofNullable(learningSessionRepository.sumDurationMinutesByUserId(userId)).orElse(0);
        long totalSessions = 0;
        for (LearningItem item : allItems) {
            totalSessions += learningSessionRepository.countByLearningItemIdAndUserId(item.getId(), userId);
        }

        return new LearningAnalyticsResponse(
                activityScore,
                activeItems,
                completedItems,
                totalMinutes,
                totalSessions
        );
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
