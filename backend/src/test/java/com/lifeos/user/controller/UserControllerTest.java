package com.lifeos.user.controller;

import com.lifeos.calendar.entity.CalendarEvent;
import com.lifeos.calendar.entity.EventType;
import com.lifeos.calendar.repository.CalendarEventRepository;
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
import com.lifeos.notes.entity.Note;
import com.lifeos.notes.repository.NoteRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    private CalendarEventRepository calendarEventRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CookieService cookieService;

    private User testUser;
    private Cookie authCookie;

    @BeforeEach
    void setUp() {
        cleanup();

        testUser = userRepository.save(new User("Test User", "user@example.com", "hash"));
        String token = tokenProvider.generateToken(testUser.getId(), testUser.getEmail());
        authCookie = new Cookie(cookieService.getCookieName(), token);
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        noteRepository.deleteAll();
        calendarEventRepository.deleteAll();
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
    @DisplayName("GET /api/v1/users/me: Should return 401 when unauthenticated")
    void getProfileSummary_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/users/me: Should return user profile summary and total entity counts")
    void getProfileSummary_Success() throws Exception {
        // Create user records
        Goal goal = goalRepository.save(new Goal(testUser, "Goal 1", GoalCategory.CAREER));
        taskRepository.save(new Task(testUser, "Task 1"));
        taskRepository.save(new Task(testUser, "Task 2"));
        habitRepository.save(new Habit(testUser, "Habit 1", HabitFrequencyType.DAILY, 7));
        learningItemRepository.save(new LearningItem(testUser, "Learn 1", LearningCategory.TECHNICAL, 100));
        noteRepository.save(new Note(testUser, "Note 1", "Content", "Tech"));

        mockMvc.perform(get("/api/v1/users/me")
                .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.totalGoals").value(1))
                .andExpect(jsonPath("$.totalTasks").value(2))
                .andExpect(jsonPath("$.totalHabits").value(1))
                .andExpect(jsonPath("$.totalLearningItems").value(1))
                .andExpect(jsonPath("$.totalNotes").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/me: Should return 401 when unauthenticated")
    void deleteCurrentUser_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/me: Should return 403 when CSRF token is missing")
    void deleteCurrentUser_MissingCsrf_Returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me")
                .cookie(authCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/me: Should transactionally delete user and all cascaded child records, clearing auth cookie")
    void deleteCurrentUser_Success_PurgesAllUserData() throws Exception {
        // Seed full domain ecosystem for user
        Goal goal = goalRepository.save(new Goal(testUser, "Full Goal", GoalCategory.CAREER));
        Task task = taskRepository.save(new Task(testUser, "Goal Task"));
        task.setGoal(goal);
        taskRepository.save(task);

        Habit habit = habitRepository.save(new Habit(testUser, "Daily Habit", HabitFrequencyType.DAILY, 7));
        HabitLog log = habitLogRepository.save(new HabitLog(habit, testUser, LocalDate.now()));
        HabitPauseInterval pause = pauseIntervalRepository.save(new HabitPauseInterval(habit, testUser, Instant.now()));

        LearningItem learnItem = learningItemRepository.save(new LearningItem(testUser, "Spring", LearningCategory.TECHNICAL, 100));
        LearningSession session = learningSessionRepository.save(new LearningSession(learnItem, testUser, LocalDate.now(), 45, "DI", "Notes"));

        CalendarEvent event = calendarEventRepository.save(new CalendarEvent(testUser, "Meeting", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT));
        Note note = noteRepository.save(new Note(testUser, "My Note", "Body", "General"));

        // Execute DELETE with valid CSRF & Auth cookie
        mockMvc.perform(delete("/api/v1/users/me")
                .cookie(authCookie)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Account and all associated data have been permanently deleted.")))
                .andExpect(cookie().value(cookieService.getCookieName(), ""))
                .andExpect(cookie().maxAge(cookieService.getCookieName(), 0));

        // Verify database: User and all cascaded data are gone
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
        assertThat(goalRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId())).isEmpty();
        assertThat(taskRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId())).isEmpty();
        assertThat(habitRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId())).isEmpty();
        assertThat(learningItemRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId())).isEmpty();
        assertThat(calendarEventRepository.findByUserIdOrderByStartTimeAsc(testUser.getId())).isEmpty();
        assertThat(noteRepository.findByUserIdOrderByUpdatedAtDesc(testUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/users/me: Tenant isolation - User A deletion does NOT delete User B's data")
    void deleteCurrentUser_TenantIsolation_DoesNotTouchUserBData() throws Exception {
        User userB = userRepository.save(new User("User B", "userb@example.com", "hash"));
        Goal goalB = goalRepository.save(new Goal(userB, "Goal B", GoalCategory.CAREER));
        Task taskB = taskRepository.save(new Task(userB, "Task B"));

        // Delete User A
        mockMvc.perform(delete("/api/v1/users/me")
                .cookie(authCookie)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        // User A gone
        assertThat(userRepository.findById(testUser.getId())).isEmpty();

        // User B intact
        assertThat(userRepository.findById(userB.getId())).isPresent();
        assertThat(goalRepository.findByUserIdOrderByCreatedAtDesc(userB.getId())).hasSize(1);
        assertThat(taskRepository.findByUserIdOrderByCreatedAtDesc(userB.getId())).hasSize(1);
    }
}
