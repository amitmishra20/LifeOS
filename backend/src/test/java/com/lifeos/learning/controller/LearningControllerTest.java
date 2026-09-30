package com.lifeos.learning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.learning.dto.CreateLearningItemRequest;
import com.lifeos.learning.dto.CreateLearningSessionRequest;
import com.lifeos.learning.dto.UpdateLearningItemRequest;
import com.lifeos.learning.dto.UpdateLearningProgressRequest;
import com.lifeos.learning.dto.UpdateLearningStatusRequest;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.entity.LearningStatus;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LearningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private LearningItemRepository learningItemRepository;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private Cookie authCookie1;
    private Cookie authCookie2;
    private Goal user1Goal;
    private Goal user2Goal;

    @BeforeEach
    void setUp() {
        cleanup();

        user1 = userRepository.save(new User("Learning Alice", "learn.alice@example.com", "hash1"));
        user2 = userRepository.save(new User("Learning Bob", "learn.bob@example.com", "hash2"));

        String token1 = tokenProvider.generateToken(user1.getId(), user1.getEmail());
        String token2 = tokenProvider.generateToken(user2.getId(), user2.getEmail());

        authCookie1 = new Cookie(cookieService.getCookieName(), token1);
        authCookie2 = new Cookie(cookieService.getCookieName(), token2);

        user1Goal = goalRepository.save(new Goal(user1, "Master Cloud", GoalCategory.CAREER));
        user2Goal = goalRepository.save(new Goal(user2, "Bob Goal", GoalCategory.HEALTH));
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        learningSessionRepository.deleteAll();
        learningItemRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =========================================================================
    // 1. AUTH TESTS
    // =========================================================================

    @Test
    @DisplayName("Should reject unauthenticated access with 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/learning"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/learning")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Rust\"}"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // 2. OWNERSHIP TESTS
    // =========================================================================

    @Test
    @DisplayName("Should return 404 when accessing another user's learning item")
    void crossUserItemRead_Returns404() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Alice System Design", LearningCategory.TECHNICAL, 100));

        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie2))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when updating another user's learning item")
    void crossUserItemUpdate_Returns404() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Alice Rust", LearningCategory.TECHNICAL, 100));

        UpdateLearningItemRequest request = new UpdateLearningItemRequest("Hacked Title", LearningCategory.TECHNICAL, 100);

        mockMvc.perform(put("/api/v1/learning/" + item.getId())
                        .cookie(authCookie2)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting another user's learning item")
    void crossUserItemDelete_Returns404() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Alice Math", LearningCategory.ACADEMIC, 100));

        mockMvc.perform(delete("/api/v1/learning/" + item.getId())
                        .cookie(authCookie2)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when logging session on another user's learning item")
    void crossUserSessionLogging_Returns404() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Alice AI", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest request = new CreateLearningSessionRequest(
                LocalDate.now(), 45, "Neural Networks", "Deep diving"
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie2)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting another user's learning session")
    void crossUserSessionDeletion_Returns404() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Alice Japanese", LearningCategory.LANGUAGE, 100));
        LearningSession session = learningSessionRepository.save(new LearningSession(
                item, user1, LocalDate.now(), 30, "Hiragana", null
        ));

        mockMvc.perform(delete("/api/v1/learning/" + item.getId() + "/sessions/" + session.getId())
                        .cookie(authCookie2)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when linking another user's goal")
    void crossUserGoalReference_Returns404() throws Exception {
        CreateLearningItemRequest request = new CreateLearningItemRequest("Compilers", LearningCategory.TECHNICAL, 100);
        request.setGoalId(user2Goal.getId()); // Bob's goal referenced by Alice

        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // 3. STATUS TRANSITION TESTS
    // =========================================================================

    @Test
    @DisplayName("Should allow all approved status transitions")
    void allowedStatusTransitions_Succeed() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Distributed Systems", LearningCategory.TECHNICAL, 100));

        // ACTIVE -> PAUSED
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 200);

        // PAUSED -> ACTIVE
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);

        // ACTIVE -> COMPLETED
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 200);

        // COMPLETED -> ACTIVE
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);

        // ACTIVE -> ARCHIVED
        performStatusTransition(item.getId(), LearningStatus.ARCHIVED, 200);

        // ARCHIVED -> ACTIVE
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);

        // PAUSED -> ARCHIVED
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 200);
        performStatusTransition(item.getId(), LearningStatus.ARCHIVED, 200);

        // ARCHIVED -> ACTIVE -> COMPLETED -> ARCHIVED
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 200);
        performStatusTransition(item.getId(), LearningStatus.ARCHIVED, 200);
    }

    @Test
    @DisplayName("Should reject illegal status transitions with 400 Bad Request across entire transition matrix")
    void disallowedStatusTransitions_Returns400() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Kubernetes", LearningCategory.TECHNICAL, 100));

        // 1. ACTIVE -> ACTIVE (disallowed)
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 400);

        // Transition to PAUSED
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 200);

        // 2. PAUSED -> PAUSED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 400);

        // 3. PAUSED -> COMPLETED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 400);

        // Resume to ACTIVE, then to COMPLETED
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 200);

        // 4. COMPLETED -> COMPLETED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 400);

        // 5. COMPLETED -> PAUSED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 400);

        // Transition to ARCHIVED
        performStatusTransition(item.getId(), LearningStatus.ARCHIVED, 200);

        // 6. ARCHIVED -> ARCHIVED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.ARCHIVED, 400);

        // 7. ARCHIVED -> PAUSED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.PAUSED, 400);

        // 8. ARCHIVED -> COMPLETED (disallowed)
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 400);
    }

    @Test
    @DisplayName("COMPLETED -> ACTIVE transition preserves session history")
    void completedToActive_PreservesSessions() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "German B1", LearningCategory.LANGUAGE, 100));
        learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 60, "Grammar", "A2 review"));
        learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 45, "Vocabulary", "Flashcards"));

        // Complete the item
        performStatusTransition(item.getId(), LearningStatus.COMPLETED, 200);

        // Re-activate the item
        performStatusTransition(item.getId(), LearningStatus.ACTIVE, 200);

        // Verify sessions are intact
        mockMvc.perform(get("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private void performStatusTransition(Long itemId, LearningStatus status, int expectedHttp) throws Exception {
        UpdateLearningStatusRequest req = new UpdateLearningStatusRequest(status);
        mockMvc.perform(patch("/api/v1/learning/" + itemId + "/status")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is(expectedHttp));
    }

    // =========================================================================
    // 4. PROGRESS TESTS
    // =========================================================================

    @Test
    @DisplayName("Should accept valid progress 0 and 100, and reject invalid progress outside 0-100")
    void progressValidation_BoundariesAndRejections() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Calculus", LearningCategory.ACADEMIC, 100));

        // 0 is valid
        mockMvc.perform(patch("/api/v1/learning/" + item.getId() + "/progress")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentProgress\": 0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentProgress").value(0));

        // 100 is valid
        mockMvc.perform(patch("/api/v1/learning/" + item.getId() + "/progress")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentProgress\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentProgress").value(100));

        // < 0 is rejected (400)
        mockMvc.perform(patch("/api/v1/learning/" + item.getId() + "/progress")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentProgress\": -1}"))
                .andExpect(status().isBadRequest());

        // > 100 is rejected (400)
        mockMvc.perform(patch("/api/v1/learning/" + item.getId() + "/progress")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentProgress\": 101}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Setting 100% progress must NOT automatically complete status")
    void reachingTargetProgress_DoesNotAutoComplete() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Piano", LearningCategory.CREATIVE, 100));

        mockMvc.perform(patch("/api/v1/learning/" + item.getId() + "/progress")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentProgress\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentProgress").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE")); // Still ACTIVE!

        LearningItem refreshed = learningItemRepository.findById(item.getId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(LearningStatus.ACTIVE);
    }

    @Test
    @DisplayName("Target progress validation on create and update")
    void targetProgressValidation() throws Exception {
        // Valid targetProgress 1
        CreateLearningItemRequest req1 = new CreateLearningItemRequest("Minimal Target", LearningCategory.PERSONAL, 1);
        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetProgress").value(1));

        // Valid targetProgress 100
        CreateLearningItemRequest req100 = new CreateLearningItemRequest("Max Target", LearningCategory.PERSONAL, 100);
        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req100)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetProgress").value(100));

        // Invalid targetProgress 0
        CreateLearningItemRequest req0 = new CreateLearningItemRequest("Zero Target", LearningCategory.PERSONAL, 0);
        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req0)))
                .andExpect(status().isBadRequest());

        // Invalid targetProgress 101
        CreateLearningItemRequest req101 = new CreateLearningItemRequest("Over Target", LearningCategory.PERSONAL, 101);
        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req101)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // 5. SESSION VALIDATION & SESSIONS CRUD
    // =========================================================================

    @Test
    @DisplayName("Should successfully create a valid learning session")
    void createSession_Success() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "TypeScript", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 45, "Generics & Conditional Types", "Completed exercises"
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.durationMinutes").value(45))
                .andExpect(jsonPath("$.topic").value("Generics & Conditional Types"))
                .andExpect(jsonPath("$.learningItemId").value(item.getId()));

        assertThat(learningSessionRepository.count()).isEqualTo(1);
        LearningSession session = learningSessionRepository.findAll().get(0);
        assertThat(session.getUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("Should reject session with future date")
    void createSession_FutureDate_Returns400() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Python", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now().plusDays(1), 30, "Future Topic", null
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session with zero or negative duration")
    void createSession_ZeroOrNegativeDuration_Returns400() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Go", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest reqZero = new CreateLearningSessionRequest(
                LocalDate.now(), 0, "Topic", null
        );
        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqZero)))
                .andExpect(status().isBadRequest());

        CreateLearningSessionRequest reqNeg = new CreateLearningSessionRequest(
                LocalDate.now(), -15, "Topic", null
        );
        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNeg)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject logging session for ARCHIVED item")
    void createSession_ArchivedItem_Returns400() throws Exception {
        LearningItem item = new LearningItem(user1, "Archived Course", LearningCategory.PROFESSIONAL, 100);
        item.setStatus(LearningStatus.ARCHIVED);
        learningItemRepository.save(item);

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 30, "Should Fail", null
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // 6. DERIVED METRICS TESTS
    // =========================================================================

    @Test
    @DisplayName("Should compute correct sum of minutes, hours, and session count, including when sessions are deleted")
    void derivedMetrics_CalculatedAccurately() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Data Structures", LearningCategory.TECHNICAL, 100));

        // Zero sessions initially
        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutesLearned").value(0))
                .andExpect(jsonPath("$.totalHoursLearned").value(0.0))
                .andExpect(jsonPath("$.sessionCount").value(0));

        // Add Session 1: 45 min
        LearningSession s1 = learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 45, "Trees", "Binary search tree"));

        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutesLearned").value(45))
                .andExpect(jsonPath("$.totalHoursLearned").value(0.8)) // 45 / 60 = 0.75 -> round to 0.8
                .andExpect(jsonPath("$.sessionCount").value(1));

        // Add Session 2: 75 min (Total: 120 min = 2.0 hours)
        LearningSession s2 = learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 75, "Graphs", "BFS & DFS"));

        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutesLearned").value(120))
                .andExpect(jsonPath("$.totalHoursLearned").value(2.0))
                .andExpect(jsonPath("$.sessionCount").value(2));

        // Delete Session 1: metrics immediately reflect new totals (75 min = 1.3 hours)
        mockMvc.perform(delete("/api/v1/learning/" + item.getId() + "/sessions/" + s1.getId())
                        .cookie(authCookie1)
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutesLearned").value(75))
                .andExpect(jsonPath("$.totalHoursLearned").value(1.3)) // 75 / 60 = 1.25 -> round to 1.3
                .andExpect(jsonPath("$.sessionCount").value(1));
    }

    // =========================================================================
    // 7. CASCADE DELETION & GOAL REFERENCE
    // =========================================================================

    @Test
    @DisplayName("Deleting learning item cascades to delete its sessions")
    void deleteItem_CascadesToSessions() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "DevOps", LearningCategory.TECHNICAL, 100));
        learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 30, "Docker", null));
        learningSessionRepository.save(new LearningSession(item, user1, LocalDate.now(), 40, "K8s", null));

        assertThat(learningSessionRepository.count()).isEqualTo(2);

        mockMvc.perform(delete("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1)
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(learningItemRepository.findById(item.getId())).isEmpty();
        assertThat(learningSessionRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Learning item can be created with goal or with null goal, without affecting goal progress")
    void goalReference_ContextualOnly() throws Exception {
        // Null goal allowed
        CreateLearningItemRequest reqNoGoal = new CreateLearningItemRequest("Solo Topic", LearningCategory.PERSONAL, 100);
        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNoGoal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").doesNotExist());

        // With Alice's goal
        CreateLearningItemRequest reqGoal = new CreateLearningItemRequest("Cloud Architecture", LearningCategory.PROFESSIONAL, 100);
        reqGoal.setGoalId(user1Goal.getId());

        mockMvc.perform(post("/api/v1/learning")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqGoal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").value(user1Goal.getId()))
                .andExpect(jsonPath("$.goalTitle").value("Master Cloud"));

        // Verify user1Goal remained untouched
        Goal goal = goalRepository.findById(user1Goal.getId()).orElseThrow();
        assertThat(goal.getProgress()).isEqualTo(0);
    }

    // =========================================================================
    // 8. SESSION OPTIONAL PROGRESS & GET ITEM CONTRACT
    // =========================================================================

    @Test
    @DisplayName("Logging session with optional valid newProgress updates item currentProgress without changing status")
    void createSession_WithValidNewProgress_UpdatesProgressNotStatus() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Distributed Databases", LearningCategory.TECHNICAL, 100));
        assertThat(item.getCurrentProgress()).isEqualTo(0);
        assertThat(item.getStatus()).isEqualTo(LearningStatus.ACTIVE);

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 60, "Spanner & TrueTime", "Read architecture overview", 45
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.durationMinutes").value(60));

        LearningItem refreshed = learningItemRepository.findById(item.getId()).orElseThrow();
        assertThat(refreshed.getCurrentProgress()).isEqualTo(45);
        assertThat(refreshed.getStatus()).isEqualTo(LearningStatus.ACTIVE);
    }

    @Test
    @DisplayName("Logging session with 100% newProgress updates progress to 100% but preserves ACTIVE status")
    void createSession_With100Progress_PreservesActiveStatus() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "System Architecture", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 90, "Final Project Review", "Completed capstone", 100
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        LearningItem refreshed = learningItemRepository.findById(item.getId()).orElseThrow();
        assertThat(refreshed.getCurrentProgress()).isEqualTo(100);
        assertThat(refreshed.getStatus()).isEqualTo(LearningStatus.ACTIVE); // Crucial: NO auto-complete!
    }

    @Test
    @DisplayName("Logging session with invalid newProgress (< 0 or > 100) returns 400 Bad Request")
    void createSession_WithInvalidNewProgress_Returns400() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Compilers", LearningCategory.TECHNICAL, 100));

        CreateLearningSessionRequest reqNeg = new CreateLearningSessionRequest(
                LocalDate.now(), 45, "Lexer", null, -10
        );
        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqNeg)))
                .andExpect(status().isBadRequest());

        CreateLearningSessionRequest reqOver = new CreateLearningSessionRequest(
                LocalDate.now(), 45, "Parser", null, 105
        );
        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqOver)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Logging session without newProgress leaves currentProgress untouched")
    void createSession_WithoutNewProgress_LeavesCurrentProgressUnchanged() throws Exception {
        LearningItem item = new LearningItem(user1, "Music Theory", LearningCategory.CREATIVE, 100);
        item.setCurrentProgress(30);
        learningItemRepository.save(item);

        CreateLearningSessionRequest req = new CreateLearningSessionRequest(
                LocalDate.now(), 30, "Circle of Fifths", null
        );

        mockMvc.perform(post("/api/v1/learning/" + item.getId() + "/sessions")
                        .cookie(authCookie1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        LearningItem refreshed = learningItemRepository.findById(item.getId()).orElseThrow();
        assertThat(refreshed.getCurrentProgress()).isEqualTo(30);
    }

    @Test
    @DisplayName("GET /api/v1/learning/{id} returns learning item with derived metrics and all sessions in deterministic order")
    void getItem_ReturnsAllSessionsInDeterministicOrder() throws Exception {
        LearningItem item = learningItemRepository.save(new LearningItem(user1, "Fullstack Mastery", LearningCategory.TECHNICAL, 100));

        LocalDate day1 = LocalDate.now().minusDays(3);
        LocalDate day2 = LocalDate.now().minusDays(1);
        LocalDate day3 = LocalDate.now();

        learningSessionRepository.save(new LearningSession(item, user1, day1, 30, "Session A", "First"));
        learningSessionRepository.save(new LearningSession(item, user1, day2, 45, "Session B", "Second"));
        learningSessionRepository.save(new LearningSession(item, user1, day3, 60, "Session C", "Third"));

        mockMvc.perform(get("/api/v1/learning/" + item.getId())
                        .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.totalMinutesLearned").value(135))
                .andExpect(jsonPath("$.totalHoursLearned").value(2.3)) // 135 / 60 = 2.25 -> 2.3
                .andExpect(jsonPath("$.sessionCount").value(3))
                .andExpect(jsonPath("$.sessions", hasSize(3)))
                .andExpect(jsonPath("$.sessions[0].topic").value("Session C")) // Most recent first
                .andExpect(jsonPath("$.sessions[1].topic").value("Session B"))
                .andExpect(jsonPath("$.sessions[2].topic").value("Session A"))
                .andExpect(jsonPath("$.recentSessions", hasSize(3)));
    }
}
