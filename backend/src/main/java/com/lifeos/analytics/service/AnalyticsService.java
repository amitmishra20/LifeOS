package com.lifeos.analytics.service;

import com.lifeos.analytics.dto.AnalyticsDashboardResponse;
import com.lifeos.analytics.dto.GoalAnalyticsResponse;
import com.lifeos.analytics.dto.HabitAnalyticsResponse;
import com.lifeos.analytics.dto.LearningAnalyticsResponse;
import com.lifeos.analytics.dto.ProductivityScoreResponse;
import com.lifeos.analytics.dto.TaskAnalyticsResponse;

public interface AnalyticsService {

    AnalyticsDashboardResponse getDashboardAnalytics(Long userId, Integer days);

    ProductivityScoreResponse getProductivityScore(Long userId);

    GoalAnalyticsResponse getGoalAnalytics(Long userId);

    TaskAnalyticsResponse getTaskAnalytics(Long userId, Integer days);

    HabitAnalyticsResponse getHabitAnalytics(Long userId);

    LearningAnalyticsResponse getLearningAnalytics(Long userId);
}
