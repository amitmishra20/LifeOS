package com.lifeos.goal.controller;

import com.lifeos.goal.dto.GoalHealthEvaluationDto;
import com.lifeos.goal.dto.GoalHealthOverviewResponse;
import com.lifeos.goal.service.GoalHealthService;
import com.lifeos.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goal-health")
public class GoalHealthController {

    private final GoalHealthService goalHealthService;

    public GoalHealthController(GoalHealthService goalHealthService) {
        this.goalHealthService = goalHealthService;
    }

    /**
     * Get Goal Health overview for the authenticated user:
     * GET /api/v1/goal-health
     */
    @GetMapping
    public ResponseEntity<GoalHealthOverviewResponse> getGoalHealthOverview(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalHealthOverviewResponse response = goalHealthService.getGoalHealthOverview(principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get explainable Goal Health evaluation for a specific goal:
     * GET /api/v1/goal-health/{goalId}
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalHealthEvaluationDto> getGoalHealth(
            @PathVariable Long goalId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalHealthEvaluationDto response = goalHealthService.getGoalHealth(principal.getId(), goalId);
        return ResponseEntity.ok(response);
    }
}
