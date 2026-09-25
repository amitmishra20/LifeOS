package com.lifeos.goal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.dto.CreateGoalRequest;
import com.lifeos.goal.dto.UpdateGoalRequest;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalPriority;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.security.CookieService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser1;
    private User testUser2;
    private Cookie authCookieUser1;
    private Cookie authCookieUser2;

    @BeforeEach
    void setUp() {
        cleanup();

        testUser1 = userRepository.save(new User("Goal Alice", "goal.alice@example.com", "hash1"));
        testUser2 = userRepository.save(new User("Goal Bob", "goal.bob@example.com", "hash2"));

        String token1 = tokenProvider.generateToken(testUser1.getId(), testUser1.getEmail());
        String token2 = tokenProvider.generateToken(testUser2.getId(), testUser2.getEmail());

        authCookieUser1 = new Cookie(cookieService.getCookieName(), token1);
        authCookieUser2 = new Cookie(cookieService.getCookieName(), token2);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        milestoneRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create goal successfully for authenticated user")
    void createGoal_Success() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Master System Design");
        request.setDescription("Complete distributed systems roadmap");
        request.setCategory(GoalCategory.CAREER);
        request.setPriority(GoalPriority.HIGH);
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setTargetDate(LocalDate.of(2026, 6, 30));

        mockMvc.perform(post("/api/v1/goals")
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Master System Design"))
                .andExpect(jsonPath("$.category").value("CAREER"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.progress").value(0))
                .andExpect(jsonPath("$.milestoneCount").value(0));

        assertThat(goalRepository.findByUserIdOrderByCreatedAtDesc(testUser1.getId())).hasSize(1);
    }

    @Test
    @DisplayName("Should fail goal creation when title is blank")
    void createGoal_ValidationError_BlankTitle() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("   ");
        request.setCategory(GoalCategory.CAREER);

        mockMvc.perform(post("/api/v1/goals")
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should reject unauthenticated access with 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/goals"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return only authenticated user's goals")
    void getGoals_UserIsolation() throws Exception {
        Goal goal1 = new Goal(testUser1, "Alice Goal", GoalCategory.EDUCATION);
        Goal goal2 = new Goal(testUser2, "Bob Goal", GoalCategory.HEALTH);
        goalRepository.save(goal1);
        goalRepository.save(goal2);

        mockMvc.perform(get("/api/v1/goals")
                .cookie(authCookieUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alice Goal"));
    }

    @Test
    @DisplayName("Should return 404 when querying another user's goal")
    void getGoalById_CrossUserRejection_Returns404() throws Exception {
        Goal bobsGoal = goalRepository.save(new Goal(testUser2, "Bob Secret", GoalCategory.FINANCE));

        // Alice attempts to get Bob's goal
        mockMvc.perform(get("/api/v1/goals/" + bobsGoal.getId())
                .cookie(authCookieUser1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("Should return 404 when updating another user's goal")
    void updateGoal_CrossUserRejection_Returns404() throws Exception {
        Goal bobsGoal = goalRepository.save(new Goal(testUser2, "Bob Goal", GoalCategory.FINANCE));

        UpdateGoalRequest updateRequest = new UpdateGoalRequest();
        updateRequest.setTitle("Hacked Goal");
        updateRequest.setCategory(GoalCategory.FINANCE);
        updateRequest.setPriority(GoalPriority.LOW);
        updateRequest.setStatus(GoalStatus.ACTIVE);

        mockMvc.perform(put("/api/v1/goals/" + bobsGoal.getId())
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting another user's goal")
    void deleteGoal_CrossUserRejection_Returns404() throws Exception {
        Goal bobsGoal = goalRepository.save(new Goal(testUser2, "Bob Goal", GoalCategory.FINANCE));

        mockMvc.perform(delete("/api/v1/goals/" + bobsGoal.getId())
                .with(csrf())
                .cookie(authCookieUser1))
                .andExpect(status().isNotFound());

        assertThat(goalRepository.existsById(bobsGoal.getId())).isTrue();
    }

    @Test
    @DisplayName("Should update goal attributes safely and keep status user-controlled")
    void updateGoal_Success() throws Exception {
        Goal goal = goalRepository.save(new Goal(testUser1, "Old Title", GoalCategory.PERSONAL));

        UpdateGoalRequest request = new UpdateGoalRequest();
        request.setTitle("New Title");
        request.setDescription("Updated description");
        request.setCategory(GoalCategory.CAREER);
        request.setPriority(GoalPriority.CRITICAL);
        request.setStatus(GoalStatus.PAUSED);

        mockMvc.perform(put("/api/v1/goals/" + goal.getId())
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.status").value("PAUSED"))
                .andExpect(jsonPath("$.priority").value("CRITICAL"));
    }

    @Test
    @DisplayName("Deleting goal cascades and deletes child milestones")
    void deleteGoal_CascadesMilestones() throws Exception {
        Goal goal = goalRepository.save(new Goal(testUser1, "Goal with Milestones", GoalCategory.CAREER));
        Milestone m1 = new Milestone(goal, testUser1, "M1", 0);
        Milestone m2 = new Milestone(goal, testUser1, "M2", 1);
        milestoneRepository.save(m1);
        milestoneRepository.save(m2);

        mockMvc.perform(delete("/api/v1/goals/" + goal.getId())
                .with(csrf())
                .cookie(authCookieUser1))
                .andExpect(status().isOk());

        assertThat(goalRepository.existsById(goal.getId())).isFalse();
        assertThat(milestoneRepository.countByGoalIdAndUserId(goal.getId(), testUser1.getId())).isEqualTo(0);
    }
}
