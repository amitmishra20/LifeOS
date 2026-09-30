package com.lifeos.habit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.habit.dto.CreateHabitRequest;
import com.lifeos.habit.dto.UpdateHabitRequest;
import com.lifeos.habit.dto.UpdateHabitStatusRequest;
import com.lifeos.habit.entity.Habit;
import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitLog;
import com.lifeos.habit.entity.HabitStatus;
import com.lifeos.habit.repository.HabitLogRepository;
import com.lifeos.habit.repository.HabitPauseIntervalRepository;
import com.lifeos.habit.repository.HabitRepository;
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
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    @Autowired
    private HabitPauseIntervalRepository pauseIntervalRepository;

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

        user1 = userRepository.save(new User("Habit Alice", "habit.alice@example.com", "hash1"));
        user2 = userRepository.save(new User("Habit Bob", "habit.bob@example.com", "hash2"));

        String token1 = tokenProvider.generateToken(user1.getId(), user1.getEmail());
        String token2 = tokenProvider.generateToken(user2.getId(), user2.getEmail());

        authCookie1 = new Cookie(cookieService.getCookieName(), token1);
        authCookie2 = new Cookie(cookieService.getCookieName(), token2);

        user1Goal = goalRepository.save(new Goal(user1, "Alice Goal", GoalCategory.HEALTH));
        user2Goal = goalRepository.save(new Goal(user2, "Bob Goal", GoalCategory.FINANCE));
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        pauseIntervalRepository.deleteAll();
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =========================================================================
    // AUTH TESTS
    // =========================================================================

    @Test
    @DisplayName("Should reject unauthenticated access with 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/habits"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // CREATE TESTS
    // =========================================================================

    @Test
    @DisplayName("Should reject habit creation without title with 400")
    void createHabit_MissingTitle_Returns400() throws Exception {
        CreateHabitRequest req = new CreateHabitRequest("", HabitFrequencyType.DAILY, 7);

        mockMvc.perform(post("/api/v1/habits")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should create standalone DAILY habit")
    void createStandaloneDailyHabit_Success() throws Exception {
        CreateHabitRequest req = new CreateHabitRequest("Morning Meditation", HabitFrequencyType.DAILY, null);
        req.setDescription("10 mins mindfulness");
        req.setIcon("heart");

        mockMvc.perform(post("/api/v1/habits")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Morning Meditation"))
                .andExpect(jsonPath("$.frequencyType").value("DAILY"))
                .andExpect(jsonPath("$.targetPerWeek").value(7))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.goalId").doesNotExist());
    }

    @Test
    @DisplayName("Should create goal-linked habit")
    void createGoalLinkedHabit_Success() throws Exception {
        CreateHabitRequest req = new CreateHabitRequest("Running", HabitFrequencyType.WEEKLY_TARGET, 3);
        req.setGoalId(user1Goal.getId());

        mockMvc.perform(post("/api/v1/habits")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Running"))
                .andExpect(jsonPath("$.goalId").value(user1Goal.getId()))
                .andExpect(jsonPath("$.goalTitle").value("Alice Goal"))
                .andExpect(jsonPath("$.targetPerWeek").value(3));
    }

    @Test
    @DisplayName("Should reject cross-user goal link with 404")
    void createGoalLinkedHabit_CrossUserGoal_Returns404() throws Exception {
        CreateHabitRequest req = new CreateHabitRequest("Running", HabitFrequencyType.DAILY, 7);
        req.setGoalId(user2Goal.getId()); // Bob's goal, Alice's token

        mockMvc.perform(post("/api/v1/habits")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    // =========================================================================
    // OWNERSHIP TESTS
    // =========================================================================

    @Test
    @DisplayName("Should reject cross-user read with 404")
    void getHabitById_CrossUser_Returns404() throws Exception {
        Habit bobHabit = new Habit(user2, "Bob Running", HabitFrequencyType.DAILY, 7);
        bobHabit = habitRepository.save(bobHabit);

        mockMvc.perform(get("/api/v1/habits/" + bobHabit.getId())
                .cookie(authCookie1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject cross-user update with 404")
    void updateHabit_CrossUser_Returns404() throws Exception {
        Habit bobHabit = new Habit(user2, "Bob Running", HabitFrequencyType.DAILY, 7);
        bobHabit = habitRepository.save(bobHabit);

        UpdateHabitRequest req = new UpdateHabitRequest("Hacked Title", HabitFrequencyType.DAILY, 7);

        mockMvc.perform(put("/api/v1/habits/" + bobHabit.getId())
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject cross-user toggle with 404")
    void toggleHabit_CrossUser_Returns404() throws Exception {
        Habit bobHabit = new Habit(user2, "Bob Running", HabitFrequencyType.DAILY, 7);
        bobHabit = habitRepository.save(bobHabit);

        mockMvc.perform(post("/api/v1/habits/" + bobHabit.getId() + "/toggle")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject cross-user delete with 404")
    void deleteHabit_CrossUser_Returns404() throws Exception {
        Habit bobHabit = new Habit(user2, "Bob Running", HabitFrequencyType.DAILY, 7);
        bobHabit = habitRepository.save(bobHabit);

        mockMvc.perform(delete("/api/v1/habits/" + bobHabit.getId())
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isNotFound());

        assertThat(habitRepository.existsById(bobHabit.getId())).isTrue();
    }

    @Test
    @DisplayName("Should reject cross-user status change with 404")
    void updateHabitStatus_CrossUser_Returns404() throws Exception {
        Habit bobHabit = new Habit(user2, "Bob Running", HabitFrequencyType.DAILY, 7);
        bobHabit = habitRepository.save(bobHabit);

        UpdateHabitStatusRequest req = new UpdateHabitStatusRequest(HabitStatus.PAUSED);

        mockMvc.perform(patch("/api/v1/habits/" + bobHabit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // STATUS LIFECYCLE TESTS
    // =========================================================================

    @Test
    @DisplayName("Should transition ACTIVE -> PAUSED, record paused_at, and create pause interval")
    void statusTransition_ActiveToPaused_Success() throws Exception {
        Habit habit = habitRepository.save(new Habit(user1, "Daily Reading", HabitFrequencyType.DAILY, 7));

        UpdateHabitStatusRequest req = new UpdateHabitStatusRequest(HabitStatus.PAUSED);

        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"))
                .andExpect(jsonPath("$.pausedAt").isNotEmpty());

        Habit updated = habitRepository.findById(habit.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(HabitStatus.PAUSED);
        assertThat(updated.getPausedAt()).isNotNull();

        assertThat(pauseIntervalRepository.findByHabitIdOrderByPausedAtAsc(habit.getId())).hasSize(1);
    }

    @Test
    @DisplayName("Should transition PAUSED -> ACTIVE and close pause interval")
    void statusTransition_PausedToActive_Success() throws Exception {
        Habit habit = new Habit(user1, "Daily Reading", HabitFrequencyType.DAILY, 7);
        habit.setStatus(HabitStatus.PAUSED);
        habit = habitRepository.save(habit);

        UpdateHabitStatusRequest pauseReq = new UpdateHabitStatusRequest(HabitStatus.PAUSED);
        // Put in active first so pause interval is created cleanly
        habit.setStatus(HabitStatus.ACTIVE);
        habitRepository.save(habit);

        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pauseReq)))
                .andExpect(status().isOk());

        // Now resume
        UpdateHabitStatusRequest resumeReq = new UpdateHabitStatusRequest(HabitStatus.ACTIVE);
        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resumeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.pausedAt").doesNotExist());

        Habit updated = habitRepository.findById(habit.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(HabitStatus.ACTIVE);
        assertThat(updated.getPausedAt()).isNull();

        var intervals = pauseIntervalRepository.findByHabitIdOrderByPausedAtAsc(habit.getId());
        assertThat(intervals).hasSize(1);
        assertThat(intervals.get(0).getResumedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should transition ACTIVE -> ARCHIVED and ARCHIVED -> ACTIVE")
    void statusTransition_ArchiveAndReactivate_Success() throws Exception {
        Habit habit = habitRepository.save(new Habit(user1, "Archivable", HabitFrequencyType.DAILY, 7));

        // Archive
        UpdateHabitStatusRequest archiveReq = new UpdateHabitStatusRequest(HabitStatus.ARCHIVED);
        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(archiveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        // Reactivate
        UpdateHabitStatusRequest reactivateReq = new UpdateHabitStatusRequest(HabitStatus.ACTIVE);
        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reactivateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should reject invalid transition ARCHIVED -> PAUSED with 400")
    void statusTransition_ArchivedToPaused_Returns400() throws Exception {
        Habit habit = new Habit(user1, "Archived Habit", HabitFrequencyType.DAILY, 7);
        habit.setStatus(HabitStatus.ARCHIVED);
        habit = habitRepository.save(habit);

        UpdateHabitStatusRequest req = new UpdateHabitStatusRequest(HabitStatus.PAUSED);
        mockMvc.perform(patch("/api/v1/habits/" + habit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("Should reject disallowed self-transitions ACTIVE->ACTIVE, PAUSED->PAUSED, ARCHIVED->ARCHIVED with 400")
    void statusTransition_DisallowedSelfTransitions_Return400() throws Exception {
        // ACTIVE -> ACTIVE
        Habit activeHabit = habitRepository.save(new Habit(user1, "Active Habit", HabitFrequencyType.DAILY, 7));
        mockMvc.perform(patch("/api/v1/habits/" + activeHabit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateHabitStatusRequest(HabitStatus.ACTIVE))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));

        // PAUSED -> PAUSED
        activeHabit.setStatus(HabitStatus.PAUSED);
        habitRepository.save(activeHabit);
        mockMvc.perform(patch("/api/v1/habits/" + activeHabit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateHabitStatusRequest(HabitStatus.PAUSED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));

        // ARCHIVED -> ARCHIVED
        activeHabit.setStatus(HabitStatus.ARCHIVED);
        habitRepository.save(activeHabit);
        mockMvc.perform(patch("/api/v1/habits/" + activeHabit.getId() + "/status")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateHabitStatusRequest(HabitStatus.ARCHIVED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    // =========================================================================
    // TOGGLE TESTS
    // =========================================================================

    @Test
    @DisplayName("Should toggle completion on and off")
    void toggleHabit_CreateAndDeleteLog_Success() throws Exception {
        Habit habit = habitRepository.save(new Habit(user1, "Workout", HabitFrequencyType.DAILY, 7));

        LocalDate today = LocalDate.now();

        // Toggle ON
        mockMvc.perform(post("/api/v1/habits/" + habit.getId() + "/toggle?date=" + today)
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.currentStreak").value(1));

        assertThat(habitLogRepository.findByHabitIdAndCompletionDate(habit.getId(), today)).isPresent();

        // Toggle OFF
        mockMvc.perform(post("/api/v1/habits/" + habit.getId() + "/toggle?date=" + today)
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));

        assertThat(habitLogRepository.findByHabitIdAndCompletionDate(habit.getId(), today)).isEmpty();
    }

    @Test
    @DisplayName("Should reject toggle on PAUSED habit with 400")
    void toggleHabit_PausedHabit_Returns400() throws Exception {
        Habit habit = new Habit(user1, "Paused Habit", HabitFrequencyType.DAILY, 7);
        habit.setStatus(HabitStatus.PAUSED);
        habit = habitRepository.save(habit);

        mockMvc.perform(post("/api/v1/habits/" + habit.getId() + "/toggle")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot log completion for a paused habit. Resume habit first."));
    }

    @Test
    @DisplayName("Should reject toggle on ARCHIVED habit with 400")
    void toggleHabit_ArchivedHabit_Returns400() throws Exception {
        Habit habit = new Habit(user1, "Archived Habit", HabitFrequencyType.DAILY, 7);
        habit.setStatus(HabitStatus.ARCHIVED);
        habit = habitRepository.save(habit);

        mockMvc.perform(post("/api/v1/habits/" + habit.getId() + "/toggle")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot log completion for an archived habit. Reactivate habit first."));
    }

    @Test
    @DisplayName("Should reject toggle on FUTURE date with 400")
    void toggleHabit_FutureDate_Returns400() throws Exception {
        Habit habit = habitRepository.save(new Habit(user1, "Daily Habit", HabitFrequencyType.DAILY, 7));

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        mockMvc.perform(post("/api/v1/habits/" + habit.getId() + "/toggle?date=" + tomorrow)
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot log completion for a future date."));
    }

    @Test
    @DisplayName("Should fetch today habits and structured 7-day history")
    void getTodayHabits_AndHistory_Success() throws Exception {
        Habit habit = habitRepository.save(new Habit(user1, "Daily Water", HabitFrequencyType.DAILY, 7));
        habitLogRepository.save(new HabitLog(habit, user1, LocalDate.now()));

        mockMvc.perform(get("/api/v1/habits/today")
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Daily Water"))
                .andExpect(jsonPath("$[0].completedToday").value(true))
                .andExpect(jsonPath("$[0].history", hasSize(7)));

        mockMvc.perform(get("/api/v1/habits/" + habit.getId() + "/history")
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[6].isToday").value(true))
                .andExpect(jsonPath("$[6].isCompleted").value(true));
    }
}
