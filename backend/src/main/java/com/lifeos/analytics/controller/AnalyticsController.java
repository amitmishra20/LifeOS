package com.lifeos.analytics.controller;

import com.lifeos.analytics.dto.AnalyticsDashboardResponse;
import com.lifeos.analytics.dto.GoalAnalyticsResponse;
import com.lifeos.analytics.dto.HabitAnalyticsResponse;
import com.lifeos.analytics.dto.ProductivityScoreResponse;
import com.lifeos.analytics.service.AnalyticsService;
import com.lifeos.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get consolidated analytics dashboard:
     * GET /api/v1/analytics/dashboard
     * GET /api/v1/analytics/dashboard?days=30
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard(
            @RequestParam(required = false) Integer days,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        AnalyticsDashboardResponse response = analyticsService.getDashboardAnalytics(principal.getId(), days);
        return ResponseEntity.ok(response);
    }

    /**
     * Get productivity score and weighted components:
     * GET /api/v1/analytics/productivity
     */
    @GetMapping("/productivity")
    public ResponseEntity<ProductivityScoreResponse> getProductivity(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProductivityScoreResponse response = analyticsService.getProductivityScore(principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get strategic goal and milestone progress analytics:
     * GET /api/v1/analytics/goals
     */
    @GetMapping("/goals")
    public ResponseEntity<GoalAnalyticsResponse> getGoalAnalytics(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GoalAnalyticsResponse response = analyticsService.getGoalAnalytics(principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get habit consistency analytics:
     * GET /api/v1/analytics/habits
     */
    @GetMapping("/habits")
    public ResponseEntity<HabitAnalyticsResponse> getHabitAnalytics(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        HabitAnalyticsResponse response = analyticsService.getHabitAnalytics(principal.getId());
        return ResponseEntity.ok(response);
    }
}
