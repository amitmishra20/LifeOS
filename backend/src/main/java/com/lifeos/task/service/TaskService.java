package com.lifeos.task.service;

import com.lifeos.task.dto.CreateTaskRequest;
import com.lifeos.task.dto.TaskResponse;
import com.lifeos.task.dto.UpdateTaskRequest;
import com.lifeos.task.entity.TaskStatus;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(Long userId, CreateTaskRequest request);

    List<TaskResponse> getTasks(Long userId, TaskStatus status);

    TaskResponse getTaskById(Long userId, Long taskId);

    TaskResponse updateTask(Long userId, Long taskId, UpdateTaskRequest request);

    TaskResponse toggleTaskCompletion(Long userId, Long taskId);

    void deleteTask(Long userId, Long taskId);
}
