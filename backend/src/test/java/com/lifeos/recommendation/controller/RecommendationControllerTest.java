package com.lifeos.recommendation.controller;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitLogRepository;
import com.lifeos.habit.repository.HabitPauseIntervalRepository;
import com.lifeos.habit.repository.HabitRepository;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskPriority;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private HabitPauseIntervalRepository pauseIntervalRepository;

    @Autowired
    private LearningItemRepository learningItemRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    private User testUser;
    private Cookie authCookie;

    @BeforeEach
    void setUp() {
        cleanup();

        testUser = userRepository.save(new User("Rec User", "rec.user@example.com", "hash"));
        String token = tokenProvider.generateToken(testUser.getId(), testUser.getEmail());
        authCookie = new Cookie(cookieService.getCookieName(), token);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        learningSessionRepository.deleteAll();
        learningItemRepository.deleteAll();
        habitLogRepository.deleteAll();
        pauseIntervalRepository.deleteAll();
        habitRepository.deleteAll();
        taskRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return 401 for unauthenticated request to /daily-focus")
    void getDailyFocus_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations/daily-focus"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 for unauthenticated request to /api/v1/recommendations")
    void getRecommendations_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return daily focus recommendations with primary focus and explainability")
    void getDailyFocus_Success() throws Exception {
        Goal goal = goalRepository.save(new Goal(testUser, "High Impact Goal", GoalCategory.CAREER));

        Task urgentTask = new Task(testUser, "Critical server patch");
        urgentTask.setPriority(TaskPriority.CRITICAL);
        urgentTask.setDueDate(LocalDate.now());
        urgentTask.setGoal(goal);
        taskRepository.save(urgentTask);

        Task lowTask = new Task(testUser, "Low priority cleanup");
        lowTask.setPriority(TaskPriority.LOW);
        taskRepository.save(lowTask);

        mockMvc.perform(get("/api/v1/recommendations/daily-focus")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").isNotEmpty())
                .andExpect(jsonPath("$.totalFocusItems").value(2))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.primaryFocus.task.title").value("Critical server patch"))
                .andExpect(jsonPath("$.primaryFocus.primaryReason").isNotEmpty())
                .andExpect(jsonPath("$.primaryFocus.totalScore").isNotEmpty());
    }

    @Test
    @DisplayName("Should return consolidated multi-domain recommendations")
    void getRecommendations_Success() throws Exception {
        // 1. Goal in BEHIND health
        Goal behindGoal = new Goal(testUser, "Backend Master", GoalCategory.CAREER);
        behindGoal.setStartDate(LocalDate.now().minusDays(30));
        behindGoal.setTargetDate(LocalDate.now().minusDays(1)); // overdue -> BEHIND
        behindGoal.setProgress(20);
        behindGoal.setStatus(GoalStatus.ACTIVE);
        goalRepository.save(behindGoal);

        // 2. Habit with 0 logs (<50% consistency)
        Habit habit = new Habit(testUser, "Daily DSA", HabitFrequencyType.DAILY, 7);
        habit.setStatus(HabitStatus.ACTIVE);
        habitRepository.save(habit);

        // 3. Learning item with 0 sessions
        LearningItem learnItem = new LearningItem(testUser, "Spring Cloud", LearningCategory.TECHNICAL, 100);
        learnItem.setCurrentProgress(10);
        learnItem.setStatus(LearningStatus.ACTIVE);
        learningItemRepository.save(learnItem);

        // 4. Task
        Task task = new Task(testUser, "Revise Java Memory Model");
        task.setPriority(TaskPriority.HIGH);
        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/recommendations")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedDate").isNotEmpty())
                .andExpect(jsonPath("$.dailyFocusTasks", hasSize(1)))
                .andExpect(jsonPath("$.habitNudges", hasSize(1)))
                .andExpect(jsonPath("$.habitNudges[0].title").value("Daily DSA"))
                .andExpect(jsonPath("$.learningFocus", hasSize(1)))
                .andExpect(jsonPath("$.learningFocus[0].title").value("Spring Cloud"))
                .andExpect(jsonPath("$.strategicAlerts", hasSize(1)))
                .andExpect(jsonPath("$.strategicAlerts[0].title").value("Backend Master"))
                .andExpect(jsonPath("$.totalRecommendationsCount").value(4));
    }

    @Test
    @DisplayName("Tenant isolation: User B cannot see User A's recommendations")
    void tenantIsolation_UserBCannotSeeUserARecommendations() throws Exception {
        // User A data
        Task taskA = new Task(testUser, "User A Secret Task");
        taskRepository.save(taskA);

        Habit habitA = new Habit(testUser, "User A Habit", HabitFrequencyType.DAILY, 7);
        habitRepository.save(habitA);

        // User B login
        User userB = userRepository.save(new User("User B", "userb@example.com", "hash"));
        String tokenB = tokenProvider.generateToken(userB.getId(), userB.getEmail());
        Cookie authCookieB = new Cookie(cookieService.getCookieName(), tokenB);

        mockMvc.perform(get("/api/v1/recommendations")
                .cookie(authCookieB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyFocusTasks", hasSize(0)))
                .andExpect(jsonPath("$.habitNudges", hasSize(0)))
                .andExpect(jsonPath("$.learningFocus", hasSize(0)))
                .andExpect(jsonPath("$.strategicAlerts", hasSize(0)))
                .andExpect(jsonPath("$.totalRecommendationsCount").value(0));
    }
}
