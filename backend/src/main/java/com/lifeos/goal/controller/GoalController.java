package com.lifeos.goal.controller;

import com.lifeos.auth.dto.MessageResponse;
import com.lifeos.goal.dto.CreateGoalRequest;
import com.lifeos.goal.dto.GoalDetailResponse;
import com.lifeos.goal.dto.GoalResponse;
import com.lifeos.goal.dto.UpdateGoalRequest;
import com.lifeos.goal.entity.GoalStatus;
import com.lifeos.goal.service.GoalService;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody CreateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalResponse response = goalService.createGoal(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getGoals(
            @RequestParam(required = false) GoalStatus status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<GoalResponse> responses = goalService.getGoals(principal.getId(), status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalDetailResponse> getGoalById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalDetailResponse response = goalService.getGoalById(principal.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalResponse response = goalService.updateGoal(principal.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        goalService.deleteGoal(principal.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Goal deleted successfully"));
    }
}
