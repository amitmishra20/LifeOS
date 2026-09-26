package com.lifeos.recommendation.service;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.recommendation.config.FocusScoringProperties;
import com.lifeos.recommendation.dto.DailyFocusResponse;
import com.lifeos.recommendation.dto.FocusItemResponse;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private FocusScoringProperties scoringProperties;
    private RecommendationServiceImpl recommendationService;

    private User testUser;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        scoringProperties = new FocusScoringProperties();
        recommendationService = new RecommendationServiceImpl(taskRepository, scoringProperties);
        testUser = new User("Alice", "alice@example.com", "hash");
        testUser.setId(1L);
        today = LocalDate.now();
    }

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

    @Test
    @DisplayName("Goal Health edge cases: completed goal, null target date, past target date, invalid date range")
    void goalHealthEdgeCases() {
        // 1. Goal completed -> 0
        Goal completedGoal = new Goal(testUser, "Done Goal", GoalCategory.CAREER);
        completedGoal.setStatus(GoalStatus.COMPLETED);
        assertThat(recommendationService.assessGoalHealth(completedGoal, today).score).isEqualTo(0);

        // 2. Goal with null target date -> ON_TRACK (+0)
        Goal undatedGoal = new Goal(testUser, "Undated Goal", GoalCategory.PERSONAL);
        undatedGoal.setTargetDate(null);
        assertThat(recommendationService.assessGoalHealth(undatedGoal, today).score).isEqualTo(0);

        // 3. Goal with target date in past and progress < 100 -> BEHIND (+30)
        Goal overdueGoal = new Goal(testUser, "Overdue Goal", GoalCategory.FINANCE);
        overdueGoal.setStartDate(today.minusDays(20));
        overdueGoal.setTargetDate(today.minusDays(5));
        overdueGoal.setProgress(50);
        assertThat(recommendationService.assessGoalHealth(overdueGoal, today).score).isEqualTo(30);

        // 4. Invalid date range (targetDate < startDate) -> ON_TRACK (+0)
        Goal invalidGoal = new Goal(testUser, "Invalid Date Range", GoalCategory.HEALTH);
        invalidGoal.setStartDate(today.plusDays(10));
        invalidGoal.setTargetDate(today.minusDays(5));
        assertThat(recommendationService.assessGoalHealth(invalidGoal, today).score).isEqualTo(0);

        // 5. Single day goal due today with 30% progress -> BEHIND (+30)
        Goal singleDayGoal = new Goal(testUser, "Single Day", GoalCategory.CAREER);
        singleDayGoal.setStartDate(today);
        singleDayGoal.setTargetDate(today);
        singleDayGoal.setProgress(30);
        assertThat(recommendationService.assessGoalHealth(singleDayGoal, today).score).isEqualTo(30);
    }
}
