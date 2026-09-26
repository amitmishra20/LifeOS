package com.lifeos.task.service;

import com.lifeos.common.exception.ResourceNotFoundException;
import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.repository.GoalRepository;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.repository.MilestoneRepository;
import com.lifeos.task.dto.CreateTaskRequest;
import com.lifeos.task.dto.TaskResponse;
import com.lifeos.task.dto.UpdateTaskRequest;
import com.lifeos.task.entity.Task;
import com.lifeos.task.entity.TaskPriority;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.repository.TaskRepository;
import com.lifeos.user.entity.User;
import com.lifeos.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            UserRepository userRepository,
            GoalRepository goalRepository,
            MilestoneRepository milestoneRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
        this.milestoneRepository = milestoneRepository;
    }

    @Override
    @Transactional
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = null;
        Milestone milestone = null;

        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findByIdAndUserId(request.getMilestoneId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

            if (request.getGoalId() != null) {
                goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

                if (!milestone.getGoal().getId().equals(goal.getId())) {
                    throw new IllegalArgumentException("Milestone does not belong to the specified goal");
                }
            } else {
                goal = milestone.getGoal();
            }
        } else if (request.getGoalId() != null) {
            goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        }

        Task task = new Task(user, request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setEstimatedMinutes(request.getEstimatedMinutes());
        task.setGoal(goal);
        task.setMilestone(milestone);

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            task.setStatus(TaskStatus.OVERDUE);
        } else {
            task.setStatus(TaskStatus.TODO);
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long userId, TaskStatus status) {
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (status != null) {
            return tasks.stream()
                    .filter(t -> t.getEffectiveStatus() == status)
                    .map(TaskResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return TaskResponse.fromEntity(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        task.setDueDate(request.getDueDate());
        task.setEstimatedMinutes(request.getEstimatedMinutes());

        Goal goal = null;
        Milestone milestone = null;

        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findByIdAndUserId(request.getMilestoneId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

            if (request.getGoalId() != null) {
                goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

                if (!milestone.getGoal().getId().equals(goal.getId())) {
                    throw new IllegalArgumentException("Milestone does not belong to the specified goal");
                }
            } else {
                goal = milestone.getGoal();
            }
            task.setGoal(goal);
            task.setMilestone(milestone);
        } else if (request.getGoalId() != null) {
            goal = goalRepository.findByIdAndUserId(request.getGoalId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
            task.setGoal(goal);
            task.setMilestone(null);
        } else {
            task.setGoal(null);
            task.setMilestone(null);
        }

        if (request.getStatus() != null) {
            if (request.getStatus() == TaskStatus.COMPLETED) {
                task.setStatus(TaskStatus.COMPLETED);
                if (task.getCompletedAt() == null) {
                    task.setCompletedAt(Instant.now());
                }
            } else {
                task.setCompletedAt(null);
                if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                    task.setStatus(TaskStatus.OVERDUE);
                } else if (request.getStatus() == TaskStatus.OVERDUE) {
                    task.setStatus(TaskStatus.TODO);
                } else {
                    task.setStatus(request.getStatus());
                }
            }
        } else {
            if (task.getStatus() != TaskStatus.COMPLETED) {
                if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                    task.setStatus(TaskStatus.OVERDUE);
                } else if (task.getStatus() == TaskStatus.OVERDUE) {
                    task.setStatus(TaskStatus.TODO);
                }
            }
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse toggleTaskCompletion(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
            if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                task.setStatus(TaskStatus.OVERDUE);
            } else {
                task.setStatus(TaskStatus.TODO);
            }
        } else {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedAt(Instant.now());
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
