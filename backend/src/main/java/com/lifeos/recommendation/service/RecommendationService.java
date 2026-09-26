package com.lifeos.recommendation.service;

import com.lifeos.recommendation.dto.DailyFocusResponse;

public interface RecommendationService {

    DailyFocusResponse getDailyFocus(Long userId);
}
