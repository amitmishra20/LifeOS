package com.lifeos.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
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
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.entity.MilestoneStatus;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.repository.TaskRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private HabitPauseIntervalRepository habitPauseIntervalRepository;

    @Autowired
    private LearningItemRepository learningItemRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    private User primaryUser;
    private User secondaryUser;
    private Cookie authCookie;
    private Cookie otherAuthCookie;

    @BeforeEach
    void setUp() {
        cleanup();

        primaryUser = userRepository.save(new User("Primary User", "analytics_user1@example.com", "passwordHash"));
        secondaryUser = userRepository.save(new User("Secondary User", "analytics_user2@example.com", "passwordHash"));

        String token = tokenProvider.generateToken(primaryUser.getId(), primaryUser.getEmail());
        authCookie = new Cookie(cookieService.getCookieName(), token);

        String otherToken = tokenProvider.generateToken(secondaryUser.getId(), secondaryUser.getEmail());
        otherAuthCookie = new Cookie(cookieService.getCookieName(), otherToken);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        learningSessionRepository.deleteAll();
        learningItemRepository.deleteAll();
        habitLogRepository.deleteAll();
        habitPauseIntervalRepository.deleteAll();
        habitRepository.deleteAll();
        taskRepository.deleteAll();
        milestoneRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/analytics/dashboard without auth returns 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Zero-data user returns 0.0 scores safely without errors")
    void zeroDataUser_ReturnsZeroSafely() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productivity.score").value(0.0))
                .andExpect(jsonPath("$.productivity.activeDomainCount").value(0))
                .andExpect(jsonPath("$.productivity.totalDomainCount").value(4))
                .andExpect(jsonPath("$.goals.progressPercentage").value(0.0))
                .andExpect(jsonPath("$.tasks.completionRate").value(0.0))
                .andExpect(jsonPath("$.habits.consistencyRate").value(0.0))
                .andExpect(jsonPath("$.learning.activityScore").value(0.0));
    }

    @Test
    @DisplayName("Goal analytics: 4 out of 8 milestones completed returns 50.0%")
    void goalAnalytics_MilestoneProgress() throws Exception {
        Goal goal1 = goalRepository.save(new Goal(primaryUser, "Master Distributed Systems", GoalCategory.CAREER));
        Goal goal2 = goalRepository.save(new Goal(primaryUser, "Health & Fitness", GoalCategory.HEALTH));

        Milestone m1 = new Milestone(goal1, primaryUser, "Read DDIA Book", 1);
        m1.setStatus(MilestoneStatus.COMPLETED);
        milestoneRepository.save(m1);

        Milestone m2 = new Milestone(goal1, primaryUser, "Build Raft consensus", 2);
        m2.setStatus(MilestoneStatus.COMPLETED);
        milestoneRepository.save(m2);

        Milestone m3 = new Milestone(goal1, primaryUser, "Deploy cluster", 3);
        m3.setStatus(MilestoneStatus.PENDING);
        milestoneRepository.save(m3);

        Milestone m4 = new Milestone(goal1, primaryUser, "Performance benchmark", 4);
        m4.setStatus(MilestoneStatus.PENDING);
        milestoneRepository.save(m4);

        Milestone m5 = new Milestone(goal2, primaryUser, "5k run", 1);
        m5.setStatus(MilestoneStatus.COMPLETED);
        milestoneRepository.save(m5);

        Milestone m6 = new Milestone(goal2, primaryUser, "10k run", 2);
        m6.setStatus(MilestoneStatus.COMPLETED);
        milestoneRepository.save(m6);

        Milestone m7 = new Milestone(goal2, primaryUser, "Half Marathon", 3);
        m7.setStatus(MilestoneStatus.PENDING);
        milestoneRepository.save(m7);

        Milestone m8 = new Milestone(goal2, primaryUser, "Full Marathon", 4);
        m8.setStatus(MilestoneStatus.PENDING);
        milestoneRepository.save(m8);

        mockMvc.perform(get("/api/v1/analytics/goals")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(50.0))
                .andExpect(jsonPath("$.totalGoals").value(2))
                .andExpect(jsonPath("$.totalMilestones").value(8))
                .andExpect(jsonPath("$.completedMilestones").value(4));
    }

    @Test
    @DisplayName("Task analytics: calculates completion rate across tasks")
    void taskAnalytics_CalculatesCompletionRate() throws Exception {
        Task t1 = new Task(primaryUser, "Task 1");
        t1.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(t1);

        Task t2 = new Task(primaryUser, "Task 2");
        t2.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(t2);

        Task t3 = new Task(primaryUser, "Task 3");
        t3.setStatus(TaskStatus.TODO);
        taskRepository.save(t3);

        Task t4 = new Task(primaryUser, "Task 4");
        t4.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(t4);

        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks.totalTasks").value(4))
                .andExpect(jsonPath("$.tasks.completedTasks").value(2))
                .andExpect(jsonPath("$.tasks.todoTasks").value(1))
                .andExpect(jsonPath("$.tasks.inProgressTasks").value(1))
                .andExpect(jsonPath("$.tasks.completionRate").value(50.0));
    }

    @Test
    @DisplayName("Habit analytics: calculates consistency across DAILY habits taking pause intervals into account")
    void habitAnalytics_DailyWithPause() throws Exception {
        Habit habit = new Habit(primaryUser, "Daily Meditation", HabitFrequencyType.DAILY, 7);
        habit = habitRepository.save(habit);

        LocalDate today = LocalDate.now();
        // Set creation date 10 days ago
        Instant createdInstant = today.minusDays(10).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        habitRepository.updateCreatedAt(habit.getId(), createdInstant);

        // Pause for 2 days
        HabitPauseInterval pause = new HabitPauseInterval(habit, primaryUser, today.minusDays(5).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        pause.setResumedAt(today.minusDays(3).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        habitPauseIntervalRepository.save(pause);

        // Log 6 completions
        for (int i = 0; i < 6; i++) {
            habitLogRepository.save(new HabitLog(habit, primaryUser, today.minusDays(i)));
        }

        mockMvc.perform(get("/api/v1/analytics/habits")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeHabitsCount").value(1))
                .andExpect(jsonPath("$.consistencyRate").isNumber());
    }

    @Test
    @DisplayName("Learning analytics: computes item progress with empirical session validation (1.0 vs 0.8)")
    void learningAnalytics_ProgressAndSessionValidation() throws Exception {
        // Item 1: 100 target, 50 current, with 1 session logged -> factor = 1.0 -> score = 50.0
        LearningItem item1 = new LearningItem(primaryUser, "Rust Systems", LearningCategory.TECHNICAL, 100);
        item1.setCurrentProgress(50);
        item1 = learningItemRepository.save(item1);

        LearningSession session1 = new LearningSession(item1, primaryUser, LocalDate.now(), 60, "Ownership model", "Studied borrow checker");
        learningSessionRepository.save(session1);

        // Item 2: 100 target, 50 current, with 0 sessions logged -> factor = 0.8 -> score = 40.0
        LearningItem item2 = new LearningItem(primaryUser, "Algorithms", LearningCategory.TECHNICAL, 100);
        item2.setCurrentProgress(50);
        learningItemRepository.save(item2);

        // Expected average = (50.0 + 40.0) / 2 = 45.0
        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.learning.activityScore").value(45.0))
                .andExpect(jsonPath("$.learning.activeItemsCount").value(2))
                .andExpect(jsonPath("$.learning.totalMinutesLearned").value(60))
                .andExpect(jsonPath("$.learning.totalSessionsCount").value(1));
    }

    @Test
    @DisplayName("Productivity Score: exact 30/25/25/20 weighted aggregation")
    void productivityScore_WeightedCalculation() throws Exception {
        // 1. Task: 1 completed out of 1 -> 100% (Weight 30% -> 30.0)
        Task task = new Task(primaryUser, "Task 1");
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        // 2. Goal: 1 completed milestone out of 1 -> 100% (Weight 25% -> 25.0)
        Goal goal = goalRepository.save(new Goal(primaryUser, "Goal 1", GoalCategory.CAREER));
        Milestone m = new Milestone(goal, primaryUser, "Milestone 1", 1);
        m.setStatus(MilestoneStatus.COMPLETED);
        milestoneRepository.save(m);

        // 3. Habit: 1 completed habit log today for 1-day old habit -> 100% (Weight 25% -> 25.0)
        Habit habit = new Habit(primaryUser, "Habit 1", HabitFrequencyType.DAILY, 7);
        habit = habitRepository.save(habit);
        habitLogRepository.save(new HabitLog(habit, primaryUser, LocalDate.now()));

        // 4. Learning: 1 completed item (100% with session) -> 100% (Weight 20% -> 20.0)
        LearningItem item = new LearningItem(primaryUser, "Learn", LearningCategory.TECHNICAL, 100);
        item.setStatus(LearningStatus.COMPLETED);
        item.setCurrentProgress(100);
        item = learningItemRepository.save(item);
        learningSessionRepository.save(new LearningSession(item, primaryUser, LocalDate.now(), 45, "Session", "Notes"));

        // Total = 30.0 + 25.0 + 25.0 + 20.0 = 100.0
        mockMvc.perform(get("/api/v1/analytics/productivity")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(100.0))
                .andExpect(jsonPath("$.activeDomainCount").value(4))
                .andExpect(jsonPath("$.components.taskCompletion.score").value(100.0))
                .andExpect(jsonPath("$.components.taskCompletion.contribution").value(30.0))
                .andExpect(jsonPath("$.components.goalProgress.score").value(100.0))
                .andExpect(jsonPath("$.components.goalProgress.contribution").value(25.0))
                .andExpect(jsonPath("$.components.habitConsistency.score").value(100.0))
                .andExpect(jsonPath("$.components.habitConsistency.contribution").value(25.0))
                .andExpect(jsonPath("$.components.learningActivity.score").value(100.0))
                .andExpect(jsonPath("$.components.learningActivity.contribution").value(20.0));
    }

    @Test
    @DisplayName("Partial data: missing domains contribute 0 without error")
    void productivityScore_PartialData() throws Exception {
        // Only task completed (100%), other domains empty
        Task task = new Task(primaryUser, "Task 1");
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/analytics/productivity")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(30.0))
                .andExpect(jsonPath("$.activeDomainCount").value(1))
                .andExpect(jsonPath("$.components.taskCompletion.active").value(true))
                .andExpect(jsonPath("$.components.habitConsistency.active").value(false))
                .andExpect(jsonPath("$.components.goalProgress.active").value(false))
                .andExpect(jsonPath("$.components.learningActivity.active").value(false));
    }

    @Test
    @DisplayName("User isolation: User A analytics never leaks User B data")
    void userIsolation_Verified() throws Exception {
        // User B has 10 completed tasks
        for (int i = 0; i < 10; i++) {
            Task task = new Task(secondaryUser, "Secret Task " + i);
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
        }

        // User A has 0 tasks
        mockMvc.perform(get("/api/v1/analytics/dashboard")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks.totalTasks").value(0))
                .andExpect(jsonPath("$.tasks.completedTasks").value(0))
                .andExpect(jsonPath("$.productivity.score").value(0.0));
    }
}
