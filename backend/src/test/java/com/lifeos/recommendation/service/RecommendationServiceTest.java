package com.lifeos.recommendation.service;

import com.lifeos.goal.config.GoalHealthProperties;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalHealth;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.goal.service.GoalHealthService;
import com.lifeos.goal.service.GoalHealthServiceImpl;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitLog;
import com.lifeos.habit.entity.HabitPauseInterval;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitLogRepository;
import com.lifeos.habit.repository.HabitPauseIntervalRepository;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.recommendation.config.FocusScoringProperties;
import com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse;
import com.lifeos.recommendation.dto.DailyFocusResponse;
import com.lifeos.recommendation.dto.FocusItemResponse;
import com.lifeos.recommendation.dto.RecommendationType;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskPriority;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.repository.TaskRepository;
import com.lifeos.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitLogRepository habitLogRepository;

    @Mock
    private HabitPauseIntervalRepository pauseIntervalRepository;

    @Mock
    private LearningItemRepository learningItemRepository;

    @Mock
    private LearningSessionRepository learningSessionRepository;

    private FocusScoringProperties scoringProperties;
    private GoalHealthService goalHealthService;
    private RecommendationServiceImpl recommendationService;

    private User testUser;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        scoringProperties = new FocusScoringProperties();
        GoalHealthProperties healthProperties = new GoalHealthProperties();
        goalHealthService = new GoalHealthServiceImpl(goalRepository, healthProperties);
        recommendationService = new RecommendationServiceImpl(
                taskRepository,
                scoringProperties,
                goalHealthService,
                goalRepository,
                habitRepository,
                habitLogRepository,
                pauseIntervalRepository,
                learningItemRepository,
                learningSessionRepository
        );
        testUser = new User("Alice", "alice@example.com", "hash");
        testUser.setId(1L);
        today = LocalDate.now();
    }

    // =========================================================================
    // 1. LOCKED DAILY FOCUS TESTS
    // =========================================================================

    @Test
    @DisplayName("Should score overdue task with +50 and correct primary reason")
    void scoreTask_Overdue_Receives50Points() {
        Task overdueTask = new Task(testUser, "Fix critical production bug");
        overdueTask.setPriority(TaskPriority.HIGH); // +30
        overdueTask.setDueDate(today.minusDays(2)); // +50 overdue
        overdueTask.setStatus(TaskStatus.OVERDUE);

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(overdueTask));

        DailyFocusResponse response = recommendationService.getDailyFocus(1L);

        assertThat(response.getItems()).hasSize(1);
        FocusItemResponse item = response.getItems().get(0);
        assertThat(item.getPriorityScore()).isEqualTo(30);
        assertThat(item.getDeadlineScore()).isEqualTo(50);
        assertThat(item.getTotalScore()).isEqualTo(80);
        assertThat(item.getPrimaryReason()).isEqualTo("Overdue task requires immediate action");
    }

    @Test
    @DisplayName("Should score due today with +40 and goal behind with +30")
    void scoreTask_DueTodayAndGoalBehind() {
        Goal behindGoal = new Goal(testUser, "Launch Platform", GoalCategory.CAREER);
        behindGoal.setStartDate(today.minusDays(30));
        behindGoal.setTargetDate(today.plusDays(10));
        behindGoal.setProgress(10); // expected progress is ~75%, so delta is -65% -> BEHIND (+30)

        Task task = new Task(testUser, "Deploy staging build");
        task.setPriority(TaskPriority.CRITICAL); // +40
        task.setDueDate(today); // +40
        task.setGoal(behindGoal); // +30

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(task));

        DailyFocusResponse response = recommendationService.getDailyFocus(1L);

        assertThat(response.getItems()).hasSize(1);
        FocusItemResponse item = response.getItems().get(0);
        assertThat(item.getPriorityScore()).isEqualTo(40);
        assertThat(item.getDeadlineScore()).isEqualTo(40);
        assertThat(item.getGoalHealthScore()).isEqualTo(30);
        assertThat(item.getTotalScore()).isEqualTo(110);
    }

    @Test
    @DisplayName("Should limit daily focus to top 5 items in deterministic order")
    void top5Limit_AndDeterministicOrdering() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Task t = new Task(testUser, "Task " + i);
            t.setId((long) i);
            t.setPriority(TaskPriority.MEDIUM); // +20
            t.setDueDate(today.plusDays(i)); // deadline scores differ
            tasks.add(t);
        }

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(tasks);

        DailyFocusResponse response = recommendationService.getDailyFocus(1L);

        assertThat(response.getItems()).hasSize(5);
        assertThat(response.getTotalFocusItems()).isEqualTo(5);
        assertThat(response.getPrimaryFocus()).isNotNull();
        // Item 1 (due tomorrow) has highest deadline score (+30)
        assertThat(response.getItems().get(0).getTask().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should exclude completed tasks from daily focus")
    void excludeCompletedTasks() {
        Task completedTask = new Task(testUser, "Already Done");
        completedTask.setStatus(TaskStatus.COMPLETED);

        Task activeTask = new Task(testUser, "Still Active");
        activeTask.setStatus(TaskStatus.TODO);

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(completedTask, activeTask));

        DailyFocusResponse response = recommendationService.getDailyFocus(1L);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getTask().getTitle()).isEqualTo("Still Active");
    }

    // =========================================================================
    // 2. HABIT NUDGE TESTS
    // =========================================================================

    @Test
    @DisplayName("Daily habit: should generate recommendation when consistency < 50%, no recommendation when >= 50%")
    void habitNudge_DailyHabit_TriggersBelow50Percent() {
        Habit habitUnder = new Habit(testUser, "Daily Meditation", HabitFrequencyType.DAILY, 7);
        habitUnder.setId(10L);
        habitUnder.setStatus(HabitStatus.ACTIVE);

        Habit habitHealthy = new Habit(testUser, "Daily Hydration", HabitFrequencyType.DAILY, 7);
        habitHealthy.setId(20L);
        habitHealthy.setStatus(HabitStatus.ACTIVE);

        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE))
                .thenReturn(List.of(habitUnder, habitHealthy));

        // 2 logs for habitUnder out of 7 days = 28.5% (< 50%) -> recommended
        List<HabitLog> logs = List.of(
                new HabitLog(habitUnder, testUser, today.minusDays(1)),
                new HabitLog(habitUnder, testUser, today.minusDays(2)),
                // 5 logs for habitHealthy out of 7 days = 71.4% (>= 50%) -> not recommended
                new HabitLog(habitHealthy, testUser, today),
                new HabitLog(habitHealthy, testUser, today.minusDays(1)),
                new HabitLog(habitHealthy, testUser, today.minusDays(2)),
                new HabitLog(habitHealthy, testUser, today.minusDays(3)),
                new HabitLog(habitHealthy, testUser, today.minusDays(4))
        );

        when(habitLogRepository.findByUserIdAndCompletionDateBetween(eq(1L), any(LocalDate.class), eq(today)))
                .thenReturn(logs);
        when(pauseIntervalRepository.findByUserIdOrderByPausedAtAsc(1L)).thenReturn(Collections.emptyList());

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getHabitNudges()).hasSize(1);
        assertThat(response.getHabitNudges().get(0).getEntityId()).isEqualTo(10L);
        assertThat(response.getHabitNudges().get(0).getType()).isEqualTo(RecommendationType.HABIT_NUDGE);
        assertThat(response.getHabitNudges().get(0).getPrimaryReason()).contains("29% (below 50% target threshold)");
    }

    @Test
    @DisplayName("Specific days habit: should calculate consistency over scheduled days and respect pause intervals")
    void habitNudge_SpecificDays_RespectsScheduledDaysAndPauses() {
        // Mon (1), Wed (3), Fri (5)
        Habit specificHabit = new Habit(testUser, "Gym Workout", HabitFrequencyType.SPECIFIC_DAYS, 3);
        specificHabit.setId(30L);
        specificHabit.setTargetDaysMask("1,3,5");
        specificHabit.setStatus(HabitStatus.ACTIVE);

        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE))
                .thenReturn(List.of(specificHabit));
        // 0 logs
        when(habitLogRepository.findByUserIdAndCompletionDateBetween(eq(1L), any(LocalDate.class), eq(today)))
                .thenReturn(Collections.emptyList());
        when(pauseIntervalRepository.findByUserIdOrderByPausedAtAsc(1L)).thenReturn(Collections.emptyList());

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getHabitNudges()).hasSize(1);
        assertThat(response.getHabitNudges().get(0).getEntityId()).isEqualTo(30L);
        assertThat(response.getHabitNudges().get(0).getPrimaryReason()).contains("0% (below 50% target threshold)");
    }

    @Test
    @DisplayName("Weekly target habit: should calculate consistency against targetPerWeek")
    void habitNudge_WeeklyTarget() {
        Habit weeklyHabit = new Habit(testUser, "Read Book", HabitFrequencyType.WEEKLY_TARGET, 4);
        weeklyHabit.setId(40L);
        weeklyHabit.setStatus(HabitStatus.ACTIVE);

        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE))
                .thenReturn(List.of(weeklyHabit));

        // 1 completion out of target 4 = 25% (< 50%) -> recommended
        List<HabitLog> logs = List.of(new HabitLog(weeklyHabit, testUser, today.minusDays(1)));
        when(habitLogRepository.findByUserIdAndCompletionDateBetween(eq(1L), any(LocalDate.class), eq(today)))
                .thenReturn(logs);
        when(pauseIntervalRepository.findByUserIdOrderByPausedAtAsc(1L)).thenReturn(Collections.emptyList());

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getHabitNudges()).hasSize(1);
        assertThat(response.getHabitNudges().get(0).getPrimaryReason()).contains("25% (below 50% target threshold)");
    }

    // =========================================================================
    // 3. LEARNING FOCUS TESTS
    // =========================================================================

    @Test
    @DisplayName("Learning focus: incomplete item with no session in 7 days should generate recommendation")
    void learningFocus_IncompleteStalled_GeneratesRecommendation() {
        LearningItem stalledItem = new LearningItem(testUser, "Master Spring Security", LearningCategory.TECHNICAL, 100);
        stalledItem.setId(100L);
        stalledItem.setCurrentProgress(30);
        stalledItem.setStatus(LearningStatus.ACTIVE);

        LearningItem activeItem = new LearningItem(testUser, "Algorithms", LearningCategory.TECHNICAL, 100);
        activeItem.setId(200L);
        activeItem.setCurrentProgress(40);
        activeItem.setStatus(LearningStatus.ACTIVE);

        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE))
                .thenReturn(List.of(stalledItem, activeItem));

        // activeItem has session 2 days ago; stalledItem has session 10 days ago
        LearningSession recentSession = new LearningSession(activeItem, testUser, today.minusDays(2), 60, "Trees", "Good");
        LearningSession oldSession = new LearningSession(stalledItem, testUser, today.minusDays(10), 45, "Filters", "Old");

        when(learningSessionRepository.findByUserIdOrderBySessionDateDescCreatedAtDesc(1L))
                .thenReturn(List.of(recentSession, oldSession));

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getLearningFocus()).hasSize(1);
        assertThat(response.getLearningFocus().get(0).getEntityId()).isEqualTo(100L);
        assertThat(response.getLearningFocus().get(0).getType()).isEqualTo(RecommendationType.LEARNING_FOCUS);
        assertThat(response.getLearningFocus().get(0).getPrimaryReason()).contains("No learning sessions logged in the last 10 days");
    }

    @Test
    @DisplayName("Learning focus: completed or non-active item should not generate recommendation")
    void learningFocus_CompletedOrNonActive_Excluded() {
        LearningItem completedItem = new LearningItem(testUser, "Java Basics", LearningCategory.TECHNICAL, 100);
        completedItem.setId(300L);
        completedItem.setCurrentProgress(100);
        completedItem.setStatus(LearningStatus.ACTIVE);

        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE))
                .thenReturn(List.of(completedItem));

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getLearningFocus()).isEmpty();
    }

    // =========================================================================
    // 4. STRATEGIC ALERT TESTS
    // =========================================================================

    @Test
    @DisplayName("Strategic alert: active goals with BEHIND or AT_RISK health generate recommendations")
    void strategicAlerts_BehindAndAtRiskGoals_TriggerAlerts() {
        Goal behindGoal = new Goal(testUser, "Graduate with Honors", GoalCategory.CAREER);
        behindGoal.setId(500L);
        behindGoal.setStartDate(today.minusDays(50));
        behindGoal.setTargetDate(today.minusDays(2)); // Overdue and progress < 100 -> BEHIND
        behindGoal.setProgress(60);
        behindGoal.setStatus(GoalStatus.ACTIVE);

        Goal atRiskGoal = new Goal(testUser, "Build SaaS App", GoalCategory.EDUCATION);
        atRiskGoal.setId(600L);
        atRiskGoal.setStartDate(today.minusDays(20));
        atRiskGoal.setTargetDate(today.plusDays(20));
        atRiskGoal.setProgress(40); // expected ~50%, delta -10% -> AT_RISK
        atRiskGoal.setStatus(GoalStatus.ACTIVE);

        Goal onTrackGoal = new Goal(testUser, "Fitness Goal", GoalCategory.HEALTH);
        onTrackGoal.setId(700L);
        onTrackGoal.setStartDate(today.minusDays(10));
        onTrackGoal.setTargetDate(today.plusDays(30));
        onTrackGoal.setProgress(30); // expected 25%, delta +5% -> ON_TRACK
        onTrackGoal.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findByUserIdAndStatus(1L, GoalStatus.ACTIVE))
                .thenReturn(List.of(behindGoal, atRiskGoal, onTrackGoal));

        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, HabitStatus.ACTIVE)).thenReturn(Collections.emptyList());
        when(learningItemRepository.findByUserIdAndStatusOrderByCreatedAtDesc(1L, LearningStatus.ACTIVE)).thenReturn(Collections.emptyList());

        ConsolidatedRecommendationsResponse response = recommendationService.getConsolidatedRecommendations(1L);

        assertThat(response.getStrategicAlerts()).hasSize(2);
        // BEHIND sorted before AT_RISK
        assertThat(response.getStrategicAlerts().get(0).getEntityId()).isEqualTo(500L);
        assertThat(response.getStrategicAlerts().get(0).getType()).isEqualTo(RecommendationType.STRATEGIC_ALERT);
        assertThat(response.getStrategicAlerts().get(0).getSubtitle()).contains("Behind Schedule");

        assertThat(response.getStrategicAlerts().get(1).getEntityId()).isEqualTo(600L);
        assertThat(response.getStrategicAlerts().get(1).getSubtitle()).contains("At Risk");
    }
}
