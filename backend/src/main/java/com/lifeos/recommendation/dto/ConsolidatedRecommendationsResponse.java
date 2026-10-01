package com.lifeos.recommendation.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsolidatedRecommendationsResponse {

    private LocalDate generatedDate;
    private List<FocusItemResponse> dailyFocusTasks = new ArrayList<>();
    private List<RecommendationItemDto> habitNudges = new ArrayList<>();
    private List<RecommendationItemDto> learningFocus = new ArrayList<>();
    private List<RecommendationItemDto> strategicAlerts = new ArrayList<>();
    private int totalRecommendationsCount;

    public ConsolidatedRecommendationsResponse() {
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public List<FocusItemResponse> getDailyFocusTasks() {
        return dailyFocusTasks;
    }

    public void setDailyFocusTasks(List<FocusItemResponse> dailyFocusTasks) {
        this.dailyFocusTasks = dailyFocusTasks;
    }

    public List<RecommendationItemDto> getHabitNudges() {
        return habitNudges;
    }

    public void setHabitNudges(List<RecommendationItemDto> habitNudges) {
        this.habitNudges = habitNudges;
    }

    public List<RecommendationItemDto> getLearningFocus() {
        return learningFocus;
    }

    public void setLearningFocus(List<RecommendationItemDto> learningFocus) {
        this.learningFocus = learningFocus;
    }

    public List<RecommendationItemDto> getStrategicAlerts() {
        return strategicAlerts;
    }

    public void setStrategicAlerts(List<RecommendationItemDto> strategicAlerts) {
        this.strategicAlerts = strategicAlerts;
    }

    public int getTotalRecommendationsCount() {
        return totalRecommendationsCount;
    }

    public void setTotalRecommendationsCount(int totalRecommendationsCount) {
        this.totalRecommendationsCount = totalRecommendationsCount;
    }
}
