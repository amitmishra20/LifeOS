package com.lifeos.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.calendar.dto.CreateEventRequest;
import com.lifeos.calendar.dto.UpdateEventRequest;
import com.lifeos.calendar.entity.CalendarEvent;
import com.lifeos.calendar.entity.EventType;
import com.lifeos.calendar.repository.CalendarEventRepository;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningSession;
import com.lifeos.learning.repository.LearningItemRepository;
import com.lifeos.learning.repository.LearningSessionRepository;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskPriority;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@DirtiesContext
class CalendarEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarEventRepository eventRepository;

    @Autowired
    private TaskRepository taskRepository;

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

    private User primaryUser;
    private User secondaryUser;
    private Cookie authCookie;
    private Cookie otherAuthCookie;

    @BeforeEach
    void setUp() {
        cleanup();

        primaryUser = userRepository.save(new User("Primary User", "primary@example.com", "passwordHash"));
        secondaryUser = userRepository.save(new User("Secondary User", "secondary@example.com", "passwordHash"));

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
        taskRepository.deleteAll();
        goalRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/calendar/events without auth returns 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/calendar/events"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/calendar/events creates new custom event")
    void createEvent_Success() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(1);

        CreateEventRequest request = new CreateEventRequest(
                "Sprint Planning",
                "Review backlog and define sprint commitments",
                start,
                end,
                EventType.CUSTOM_EVENT
        );

        mockMvc.perform(post("/api/v1/calendar/events")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Sprint Planning"))
                .andExpect(jsonPath("$.description").value("Review backlog and define sprint commitments"))
                .andExpect(jsonPath("$.eventType").value("CUSTOM_EVENT"));

        assertThat(eventRepository.findByUserIdOrderByStartTimeAsc(primaryUser.getId())).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/v1/calendar/events validation failure when missing title")
    void createEvent_MissingTitle_Returns400() throws Exception {
        CreateEventRequest request = new CreateEventRequest(
                "",
                "Description",
                LocalDateTime.now().plusDays(1),
                null,
                EventType.CUSTOM_EVENT
        );

        mockMvc.perform(post("/api/v1/calendar/events")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/calendar/events validation failure when end time is before start time")
    void createEvent_EndTimeBeforeStartTime_Returns400() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.minusHours(2);

        CreateEventRequest request = new CreateEventRequest(
                "Invalid Timing Event",
                "Description",
                start,
                end,
                EventType.CUSTOM_EVENT
        );

        mockMvc.perform(post("/api/v1/calendar/events")
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/calendar/events returns only current user's events")
    void getEvents_UserIsolation() throws Exception {
        eventRepository.save(new CalendarEvent(primaryUser, "User 1 Event", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT));
        eventRepository.save(new CalendarEvent(secondaryUser, "User 2 Event", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT));

        mockMvc.perform(get("/api/v1/calendar/events")
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("User 1 Event"));
    }

    @Test
    @DisplayName("GET /api/v1/calendar/events/{id} returns 404 for cross-user event")
    void getEvent_CrossUser_Returns404() throws Exception {
        CalendarEvent otherEvent = eventRepository.save(
                new CalendarEvent(secondaryUser, "Secret Meeting", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT)
        );

        mockMvc.perform(get("/api/v1/calendar/events/" + otherEvent.getId())
                        .cookie(authCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/calendar/events/{id} updates event successfully")
    void updateEvent_Success() throws Exception {
        CalendarEvent event = eventRepository.save(
                new CalendarEvent(primaryUser, "Initial Title", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT)
        );

        LocalDateTime updatedStart = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);
        LocalDateTime updatedEnd = updatedStart.plusHours(2);

        UpdateEventRequest updateReq = new UpdateEventRequest(
                "Updated Title",
                "Updated description",
                updatedStart,
                updatedEnd,
                EventType.CUSTOM_EVENT
        );

        mockMvc.perform(put("/api/v1/calendar/events/" + event.getId())
                        .with(csrf())
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.eventType").value("CUSTOM_EVENT"));
    }

    @Test
    @DisplayName("DELETE /api/v1/calendar/events/{id} deletes event")
    void deleteEvent_Success() throws Exception {
        CalendarEvent event = eventRepository.save(
                new CalendarEvent(primaryUser, "To Delete", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT)
        );

        mockMvc.perform(delete("/api/v1/calendar/events/" + event.getId())
                        .with(csrf())
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Event deleted successfully"));

        assertThat(eventRepository.findById(event.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/calendar/events/{id} returns 404 for cross-user event")
    void deleteEvent_CrossUser_Returns404() throws Exception {
        CalendarEvent otherEvent = eventRepository.save(
                new CalendarEvent(secondaryUser, "Other's Event", LocalDateTime.now().plusDays(1), null, EventType.CUSTOM_EVENT)
        );

        mockMvc.perform(delete("/api/v1/calendar/events/" + otherEvent.getId())
                        .with(csrf())
                        .cookie(authCookie))
                .andExpect(status().isNotFound());

        assertThat(eventRepository.findById(otherEvent.getId())).isPresent();
    }

    @Test
    @DisplayName("GET /api/v1/calendar?start=...&end=... returns unified calendar feed with events, tasks, goals, learning")
    void getUnifiedCalendarFeed_AggregatesAllDomains() throws Exception {
        LocalDate today = LocalDate.now();

        // 1. Custom event
        eventRepository.save(new CalendarEvent(
                primaryUser, "Project Kickoff", today.plusDays(1).atTime(10, 0), today.plusDays(1).atTime(11, 0), EventType.CUSTOM_EVENT
        ));

        // 2. Task with due date
        Task task = new Task(primaryUser, "Ship MVP release");
        task.setDueDate(today.plusDays(2));
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.TODO);
        taskRepository.save(task);

        // 3. Goal with target date
        Goal goal = new Goal(primaryUser, "Achieve Health Milestone", GoalCategory.HEALTH);
        goal.setTargetDate(today.plusDays(3));
        goalRepository.save(goal);

        // 4. Learning Session
        LearningItem item = new LearningItem(primaryUser, "Advanced Java", LearningCategory.TECHNICAL, 100);
        learningItemRepository.save(item);

        LearningSession session = new LearningSession(item, primaryUser, today.plusDays(4), 60, "Concurrency in JVM", "Deep dive into virtual threads");
        learningSessionRepository.save(session);

        mockMvc.perform(get("/api/v1/calendar")
                        .param("start", today.toString())
                        .param("end", today.plusDays(10).toString())
                        .cookie(authCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].itemType").value("CUSTOM_EVENT"))
                .andExpect(jsonPath("$[0].title").value("Project Kickoff"))
                .andExpect(jsonPath("$[1].itemType").value("TASK_DEADLINE"))
                .andExpect(jsonPath("$[1].title").value("Task: Ship MVP release"))
                .andExpect(jsonPath("$[2].itemType").value("GOAL_DEADLINE"))
                .andExpect(jsonPath("$[2].title").value("Goal Target: Achieve Health Milestone"))
                .andExpect(jsonPath("$[3].itemType").value("LEARNING_SESSION"))
                .andExpect(jsonPath("$[3].title").value("Study: Advanced Java (Concurrency in JVM)"));
    }
}
