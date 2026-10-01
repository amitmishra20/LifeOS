package com.lifeos.recommendation.controller;

import com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse;
import com.lifeos.recommendation.dto.DailyFocusResponse;
import com.lifeos.recommendation.service.RecommendationService;
import com.lifeos.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/daily-focus")
    public ResponseEntity<DailyFocusResponse> getDailyFocus(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        DailyFocusResponse response = recommendationService.getDailyFocus(principal.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse> getRecommendations(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse response =
                recommendationService.getConsolidatedRecommendations(principal.getId());
        return ResponseEntity.ok(response);
    }
}
