package com.lifeos.goal.controller;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalPriority;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.security.TokenProvider;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoalHealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private User testUser1;
    private User testUser2;
    private Cookie authCookieUser1;
    private Cookie authCookieUser2;

    @BeforeEach
    void setUp() {
        testUser1 = userRepository.save(new User("Alice Health", "alice.health@example.com", "$2a$10$hashedpassword"));
        testUser2 = userRepository.save(new User("Bob Health", "bob.health@example.com", "$2a$10$hashedpassword"));

        String token1 = tokenProvider.generateToken(testUser1.getId(), testUser1.getEmail());
        String token2 = tokenProvider.generateToken(testUser2.getId(), testUser2.getEmail());

        authCookieUser1 = new Cookie("lifeos_token", token1);
        authCookieUser2 = new Cookie("lifeos_token", token2);
    }

    @AfterEach
    void tearDown() {
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/goal-health - Unauthenticated returns 401")
    void getOverview_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/goal-health"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/goal-health - Authenticated returns aggregated overview")
    void getOverview_Authenticated_ReturnsCalculatedOverview() throws Exception {
        LocalDate today = LocalDate.now();

        // Goal 1: Completed
        Goal g1 = new Goal(testUser1, "Launch v1", GoalCategory.CAREER);
        g1.setStatus(GoalStatus.COMPLETED);
        g1.setProgress(100);
        g1.setStartDate(today.minusDays(10));
        g1.setTargetDate(today.plusDays(10));
        goalRepository.save(g1);

        // Goal 2: On Track (50% progress, 50% elapsed)
        Goal g2 = new Goal(testUser1, "Fitness Journey", GoalCategory.HEALTH);
        g2.setStatus(GoalStatus.ACTIVE);
        g2.setProgress(50);
        g2.setStartDate(today.minusDays(10));
        g2.setTargetDate(today.plusDays(10));
        goalRepository.save(g2);

        // Goal 3 for User 2 (Isolation check: must not be in User 1's overview)
        Goal g3 = new Goal(testUser2, "Bob Goal", GoalCategory.PERSONAL);
        g3.setStatus(GoalStatus.ACTIVE);
        g3.setProgress(0);
        goalRepository.save(g3);

        mockMvc.perform(get("/api/v1/goal-health")
                        .cookie(authCookieUser1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authCookieUser1.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalGoals").value(2))
                .andExpect(jsonPath("$.completedCount").value(1))
                .andExpect(jsonPath("$.onTrackCount").value(1))
                .andExpect(jsonPath("$.atRiskCount").value(0))
                .andExpect(jsonPath("$.behindCount").value(0))
                .andExpect(jsonPath("$.evaluations", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/v1/goal-health/{id} - Owner returns explainable evaluation")
    void getGoalHealth_Owner_ReturnsDetails() throws Exception {
        LocalDate today = LocalDate.now();

        Goal goal = new Goal(testUser1, "Learn Java 21", GoalCategory.EDUCATION);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setProgress(30);
        goal.setStartDate(today.minusDays(10));
        goal.setTargetDate(today.plusDays(10));
        Goal saved = goalRepository.save(goal);

        mockMvc.perform(get("/api/v1/goal-health/" + saved.getId())
                        .cookie(authCookieUser1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authCookieUser1.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalId").value(saved.getId()))
                .andExpect(jsonPath("$.goalTitle").value("Learn Java 21"))
                .andExpect(jsonPath("$.category").value("EDUCATION"))
                .andExpect(jsonPath("$.actualProgress").value(30.0))
                .andExpect(jsonPath("$.expectedProgress").value(50.0))
                .andExpect(jsonPath("$.delta").value(-20.0))
                .andExpect(jsonPath("$.health").value("AT_RISK"))
                .andExpect(jsonPath("$.reason").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/v1/goal-health/{id} - Cross-user access returns 404")
    void getGoalHealth_CrossUser_Returns404() throws Exception {
        Goal user1Goal = new Goal(testUser1, "Secret Goal", GoalCategory.PERSONAL);
        Goal saved = goalRepository.save(user1Goal);

        // User 2 attempts to fetch User 1's goal health
        mockMvc.perform(get("/api/v1/goal-health/" + saved.getId())
                        .cookie(authCookieUser2)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authCookieUser2.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/goal-health/{id} - Non-existent goal returns 404")
    void getGoalHealth_NonExistent_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/goal-health/999999")
                        .cookie(authCookieUser1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authCookieUser1.getValue())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
