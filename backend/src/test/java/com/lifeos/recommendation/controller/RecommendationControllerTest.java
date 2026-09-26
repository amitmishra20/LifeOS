package com.lifeos.recommendation.controller;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
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
}
