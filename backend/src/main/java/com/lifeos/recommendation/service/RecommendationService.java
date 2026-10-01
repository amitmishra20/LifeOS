package com.lifeos.recommendation.service;

import com.lifeos.recommendation.dto.ConsolidatedRecommendationsResponse;
import com.lifeos.recommendation.dto.DailyFocusResponse;

public interface RecommendationService {

    DailyFocusResponse getDailyFocus(Long userId);

    ConsolidatedRecommendationsResponse getConsolidatedRecommendations(Long userId);
}
