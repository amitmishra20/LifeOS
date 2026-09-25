package com.lifeos.milestone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.milestone.dto.CreateMilestoneRequest;
import com.lifeos.milestone.dto.UpdateMilestoneRequest;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.entity.MilestoneStatus;
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
class MilestoneControllerTest {

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
    private Goal user1Goal;
    private Goal user2Goal;

    @BeforeEach
    void setUp() {
        cleanup();

        testUser1 = userRepository.save(new User("Milestone Alice", "milestone.alice@example.com", "hash1"));
        testUser2 = userRepository.save(new User("Milestone Bob", "milestone.bob@example.com", "hash2"));

        String token1 = tokenProvider.generateToken(testUser1.getId(), testUser1.getEmail());
        String token2 = tokenProvider.generateToken(testUser2.getId(), testUser2.getEmail());

        authCookieUser1 = new Cookie(cookieService.getCookieName(), token1);
        authCookieUser2 = new Cookie(cookieService.getCookieName(), token2);

        user1Goal = goalRepository.save(new Goal(testUser1, "Alice Goal", GoalCategory.CAREER));
        user2Goal = goalRepository.save(new Goal(testUser2, "Bob Goal", GoalCategory.PERSONAL));
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
    @DisplayName("Should create milestone under parent goal and assign next order_index")
    void createMilestone_Success() throws Exception {
        CreateMilestoneRequest req1 = new CreateMilestoneRequest("Milestone 1");
        CreateMilestoneRequest req2 = new CreateMilestoneRequest("Milestone 2");

        mockMvc.perform(post("/api/v1/goals/" + user1Goal.getId() + "/milestones")
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Milestone 1"))
                .andExpect(jsonPath("$.orderIndex").value(0))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.progress").value(0));

        mockMvc.perform(post("/api/v1/goals/" + user1Goal.getId() + "/milestones")
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Milestone 2"))
                .andExpect(jsonPath("$.orderIndex").value(1));

        Goal refreshedGoal = goalRepository.findById(user1Goal.getId()).orElseThrow();
        assertThat(refreshedGoal.getProgress()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reject milestone creation under another user's goal with 404")
    void createMilestone_CrossUserRejection_Returns404() throws Exception {
        CreateMilestoneRequest req = new CreateMilestoneRequest("Malicious Milestone");

        // Alice attempts to create milestone under Bob's goal
        mockMvc.perform(post("/api/v1/goals/" + user2Goal.getId() + "/milestones")
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @DisplayName("Should reject unauthenticated milestone access with 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/goals/" + user1Goal.getId() + "/milestones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 404 when querying another user's milestone")
    void getMilestoneById_CrossUserRejection_Returns404() throws Exception {
        Milestone bobsMilestone = milestoneRepository.save(
                new Milestone(user2Goal, testUser2, "Bobs Secret", 0)
        );

        mockMvc.perform(get("/api/v1/milestones/" + bobsMilestone.getId())
                .cookie(authCookieUser1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when updating another user's milestone")
    void updateMilestone_CrossUserRejection_Returns404() throws Exception {
        Milestone bobsMilestone = milestoneRepository.save(
                new Milestone(user2Goal, testUser2, "Bobs Milestone", 0)
        );

        UpdateMilestoneRequest updateReq = new UpdateMilestoneRequest();
        updateReq.setTitle("Hacked Milestone");
        updateReq.setStatus(MilestoneStatus.COMPLETED);

        mockMvc.perform(put("/api/v1/milestones/" + bobsMilestone.getId())
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting another user's milestone")
    void deleteMilestone_CrossUserRejection_Returns404() throws Exception {
        Milestone bobsMilestone = milestoneRepository.save(
                new Milestone(user2Goal, testUser2, "Bobs Milestone", 0)
        );

        mockMvc.perform(delete("/api/v1/milestones/" + bobsMilestone.getId())
                .with(csrf())
                .cookie(authCookieUser1))
                .andExpect(status().isNotFound());

        assertThat(milestoneRepository.existsById(bobsMilestone.getId())).isTrue();
    }

    @Test
    @DisplayName("Deterministic Goal Progress: completed / total * 100, and 100% progress does NOT change Goal Status")
    void goalProgressRecalculation_AndGoalStatusIntegrity() throws Exception {
        // Create 2 milestones under Alice's goal
        Milestone m1 = milestoneRepository.save(new Milestone(user1Goal, testUser1, "Phase 1", 0));
        Milestone m2 = milestoneRepository.save(new Milestone(user1Goal, testUser1, "Phase 2", 1));

        // Initial progress recalculation: 0/2 = 0%
        mockMvc.perform(get("/api/v1/goals/" + user1Goal.getId())
                .cookie(authCookieUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Complete Milestone 1: 1/2 = 50%
        UpdateMilestoneRequest completeM1 = new UpdateMilestoneRequest();
        completeM1.setTitle("Phase 1");
        completeM1.setStatus(MilestoneStatus.COMPLETED);

        mockMvc.perform(put("/api/v1/milestones/" + m1.getId())
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeM1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(100));

        Goal goalAfterM1 = goalRepository.findById(user1Goal.getId()).orElseThrow();
        assertThat(goalAfterM1.getProgress()).isEqualTo(50);
        assertThat(goalAfterM1.getStatus()).isEqualTo(GoalStatus.ACTIVE);

        // Complete Milestone 2: 2/2 = 100%
        UpdateMilestoneRequest completeM2 = new UpdateMilestoneRequest();
        completeM2.setTitle("Phase 2");
        completeM2.setStatus(MilestoneStatus.COMPLETED);

        mockMvc.perform(put("/api/v1/milestones/" + m2.getId())
                .with(csrf())
                .cookie(authCookieUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeM2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(100));

        Goal goalAfterM2 = goalRepository.findById(user1Goal.getId()).orElseThrow();
        // Progress reaches 100%
        assertThat(goalAfterM2.getProgress()).isEqualTo(100);
        // CRITICAL: Goal Status MUST REMAIN ACTIVE (user-controlled, never silently auto-completed!)
        assertThat(goalAfterM2.getStatus()).isEqualTo(GoalStatus.ACTIVE);

        // Delete Milestone 2: now 1/1 = 100%
        mockMvc.perform(delete("/api/v1/milestones/" + m2.getId())
                .with(csrf())
                .cookie(authCookieUser1))
                .andExpect(status().isOk());

        Goal goalAfterDeleteM2 = goalRepository.findById(user1Goal.getId()).orElseThrow();
        assertThat(goalAfterDeleteM2.getProgress()).isEqualTo(100);

        // Delete Milestone 1: now 0 milestones = 0%
        mockMvc.perform(delete("/api/v1/milestones/" + m1.getId())
                .with(csrf())
                .cookie(authCookieUser1))
                .andExpect(status().isOk());

        Goal goalZeroMilestones = goalRepository.findById(user1Goal.getId()).orElseThrow();
        assertThat(goalZeroMilestones.getProgress()).isEqualTo(0);
    }

    @Test
    @DisplayName("Milestones are ordered deterministically by order_index ASC then created_at ASC")
    void getMilestonesByGoal_Ordering() throws Exception {
        Milestone m2 = new Milestone(user1Goal, testUser1, "Second", 2);
        Milestone m0 = new Milestone(user1Goal, testUser1, "Zeroth", 0);
        Milestone m1 = new Milestone(user1Goal, testUser1, "First", 1);
        milestoneRepository.save(m2);
        milestoneRepository.save(m0);
        milestoneRepository.save(m1);

        mockMvc.perform(get("/api/v1/goals/" + user1Goal.getId() + "/milestones")
                .cookie(authCookieUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("Zeroth"))
                .andExpect(jsonPath("$[1].title").value("First"))
                .andExpect(jsonPath("$[2].title").value("Second"));
    }
}
