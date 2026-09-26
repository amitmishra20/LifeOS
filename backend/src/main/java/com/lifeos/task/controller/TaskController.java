package com.lifeos.task.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.security.UserPrincipal;
import com.lifeos.task.dto.CreateTaskRequest;
import com.lifeos.task.dto.TaskResponse;
import com.lifeos.task.dto.UpdateTaskRequest;
import com.lifeos.task.entity.TaskStatus;
import com.lifeos.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TaskResponse response = taskService.createTask(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<TaskResponse> responses = taskService.getTasks(principal.getId(), status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TaskResponse response = taskService.getTaskById(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TaskResponse response = taskService.updateTask(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> toggleComplete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TaskResponse response = taskService.toggleTaskCompletion(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        taskService.deleteTask(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
    }
}
