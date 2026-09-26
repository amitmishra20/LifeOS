package com.lifeos.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.security.CookieService;
import com.lifeos.security.TokenProvider;
import com.lifeos.task.dto.CreateTaskRequest;
import com.lifeos.task.dto.UpdateTaskRequest;
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
class TaskControllerTest {

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
    private Milestone user1Milestone;
    private Milestone user2Milestone;

    @BeforeEach
    void setUp() {
        cleanup();

        user1 = userRepository.save(new User("Task Alice", "task.alice@example.com", "hash1"));
        user2 = userRepository.save(new User("Task Bob", "task.bob@example.com", "hash2"));

        String token1 = tokenProvider.generateToken(user1.getId(), user1.getEmail());
        String token2 = tokenProvider.generateToken(user2.getId(), user2.getEmail());

        authCookie1 = new Cookie(cookieService.getCookieName(), token1);
        authCookie2 = new Cookie(cookieService.getCookieName(), token2);

        user1Goal = goalRepository.save(new Goal(user1, "Alice Goal", GoalCategory.CAREER));
        user2Goal = goalRepository.save(new Goal(user2, "Bob Goal", GoalCategory.PERSONAL));

        user1Milestone = milestoneRepository.save(new Milestone(user1Goal, user1, "Alice Milestone", 0));
        user2Milestone = milestoneRepository.save(new Milestone(user2Goal, user2, "Bob Milestone", 0));
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    private void cleanup() {
        taskRepository.deleteAll();
        milestoneRepository.deleteAll();
        goalRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should reject unauthenticated access with 401")
    void unauthenticatedAccess_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject task creation without title with 400")
    void createTask_MissingTitle_Returns400() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("");

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should create standalone task")
    void createStandaloneTask_Success() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Buy groceries");
        req.setDescription("Milk, bread, eggs");
        req.setPriority(TaskPriority.HIGH);
        req.setDueDate(LocalDate.now().plusDays(2));
        req.setEstimatedMinutes(30);

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.estimatedMinutes").value(30))
                .andExpect(jsonPath("$.goalId").doesNotExist());
    }

    @Test
    @DisplayName("Should create goal-linked task")
    void createGoalLinkedTask_Success() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Read research paper");
        req.setGoalId(user1Goal.getId());

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").value(user1Goal.getId()))
                .andExpect(jsonPath("$.goalTitle").value("Alice Goal"));
    }

    @Test
    @DisplayName("Should create milestone-linked task and auto-derive goal")
    void createMilestoneLinkedTask_Success() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Write chapter 1");
        req.setMilestoneId(user1Milestone.getId());

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.milestoneId").value(user1Milestone.getId()))
                .andExpect(jsonPath("$.milestoneTitle").value("Alice Milestone"))
                .andExpect(jsonPath("$.goalId").value(user1Goal.getId()))
                .andExpect(jsonPath("$.goalTitle").value("Alice Goal"));
    }

    @Test
    @DisplayName("Should return 404 when linking to another user's goal")
    void createGoalLinkedTask_CrossUserGoal_Returns404() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Sneaky task");
        req.setGoalId(user2Goal.getId());

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when linking to another user's milestone")
    void createMilestoneLinkedTask_CrossUserMilestone_Returns404() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Sneaky task");
        req.setMilestoneId(user2Milestone.getId());

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 when milestone does not belong to specified goal")
    void createGoalMilestoneMismatched_Returns400() throws Exception {
        Goal anotherGoal = goalRepository.save(new Goal(user1, "Second Goal", GoalCategory.HEALTH));

        CreateTaskRequest req = new CreateTaskRequest("Mismatched task");
        req.setGoalId(anotherGoal.getId());
        req.setMilestoneId(user1Milestone.getId()); // belongs to user1Goal

        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should enforce user isolation in getTasks")
    void getTasks_EnforcesUserIsolation() throws Exception {
        Task task1 = new Task(user1, "Alice Task");
        Task task2 = new Task(user2, "Bob Task");
        taskRepository.save(task1);
        taskRepository.save(task2);

        mockMvc.perform(get("/api/v1/tasks")
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Alice Task"));
    }

    @Test
    @DisplayName("Should update task fields properly")
    void updateTask_Success() throws Exception {
        Task task = taskRepository.save(new Task(user1, "Original Title"));

        UpdateTaskRequest updateReq = new UpdateTaskRequest();
        updateReq.setTitle("Updated Title");
        updateReq.setPriority(TaskPriority.CRITICAL);
        updateReq.setDueDate(LocalDate.now().plusDays(5));
        updateReq.setEstimatedMinutes(45);

        mockMvc.perform(put("/api/v1/tasks/" + task.getId())
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.priority").value("CRITICAL"))
                .andExpect(jsonPath("$.estimatedMinutes").value(45));
    }

    @Test
    @DisplayName("Should toggle completion and reopening deterministically")
    void toggleCompletion_Success() throws Exception {
        Task task = taskRepository.save(new Task(user1, "Focus Task"));

        // 1. Complete task
        mockMvc.perform(patch("/api/v1/tasks/" + task.getId() + "/complete")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());

        // 2. Reopen task
        mockMvc.perform(patch("/api/v1/tasks/" + task.getId() + "/complete")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.completedAt").doesNotExist());
    }

    @Test
    @DisplayName("Should set status to OVERDUE when task is past due, and reopen to OVERDUE if still past due")
    void overdueLifecycle_HandledDeterministically() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("Overdue Task");
        req.setDueDate(LocalDate.now().minusDays(3));

        // Creation with past due date -> OVERDUE
        mockMvc.perform(post("/api/v1/tasks")
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OVERDUE"));

        Task saved = taskRepository.findByUserIdOrderByCreatedAtDesc(user1.getId()).get(0);

        // Complete it -> COMPLETED
        mockMvc.perform(patch("/api/v1/tasks/" + saved.getId() + "/complete")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Reopen it -> OVERDUE (because due date is still past)
        mockMvc.perform(patch("/api/v1/tasks/" + saved.getId() + "/complete")
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OVERDUE"));

        // Reschedule to tomorrow -> transitions from OVERDUE to TODO
        UpdateTaskRequest rescheduleReq = new UpdateTaskRequest();
        rescheduleReq.setDueDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/api/v1/tasks/" + saved.getId())
                .with(csrf())
                .cookie(authCookie1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rescheduleReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("Should delete task successfully and reject cross-user deletion with 404")
    void deleteTask_SuccessAndIsolation() throws Exception {
        Task task = taskRepository.save(new Task(user1, "To Delete"));

        // User 2 cannot delete User 1's task -> 404
        mockMvc.perform(delete("/api/v1/tasks/" + task.getId())
                .with(csrf())
                .cookie(authCookie2))
                .andExpect(status().isNotFound());

        // User 1 deletes successfully -> 200
        mockMvc.perform(delete("/api/v1/tasks/" + task.getId())
                .with(csrf())
                .cookie(authCookie1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }
}
