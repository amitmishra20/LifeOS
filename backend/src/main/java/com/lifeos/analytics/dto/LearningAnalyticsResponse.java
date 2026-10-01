package com.lifeos.analytics.dto;

public class LearningAnalyticsResponse {

    private double activityScore;
    private long activeItemsCount;
    private long completedItemsCount;
    private long totalMinutesLearned;
    private long totalSessionsCount;

    public LearningAnalyticsResponse() {
    }

    public LearningAnalyticsResponse(double activityScore, long activeItemsCount, long completedItemsCount, long totalMinutesLearned, long totalSessionsCount) {
        this.activityScore = activityScore;
        this.activeItemsCount = activeItemsCount;
        this.completedItemsCount = completedItemsCount;
        this.totalMinutesLearned = totalMinutesLearned;
        this.totalSessionsCount = totalSessionsCount;
    }

    public double getActivityScore() {
        return activityScore;
    }

    public void setActivityScore(double activityScore) {
        this.activityScore = activityScore;
    }

    public long getActiveItemsCount() {
        return activeItemsCount;
    }

    public void setActiveItemsCount(long activeItemsCount) {
        this.activeItemsCount = activeItemsCount;
    }

    public long getCompletedItemsCount() {
        return completedItemsCount;
    }

    public void setCompletedItemsCount(long completedItemsCount) {
        this.completedItemsCount = completedItemsCount;
    }

    public long getTotalMinutesLearned() {
        return totalMinutesLearned;
    }

    public void setTotalMinutesLearned(long totalMinutesLearned) {
        this.totalMinutesLearned = totalMinutesLearned;
    }

    public long getTotalSessionsCount() {
        return totalSessionsCount;
    }

    public void setTotalSessionsCount(long totalSessionsCount) {
        this.totalSessionsCount = totalSessionsCount;
    }
}
