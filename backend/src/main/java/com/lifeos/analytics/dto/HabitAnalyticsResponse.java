package com.lifeos.analytics.dto;

public class HabitAnalyticsResponse {

    private double consistencyRate;
    private long activeHabitsCount;
    private long totalHabitsCount;

    public HabitAnalyticsResponse() {
    }

    public HabitAnalyticsResponse(double consistencyRate, long activeHabitsCount, long totalHabitsCount) {
        this.consistencyRate = consistencyRate;
        this.activeHabitsCount = activeHabitsCount;
        this.totalHabitsCount = totalHabitsCount;
    }

    public double getConsistencyRate() {
        return consistencyRate;
    }

    public void setConsistencyRate(double consistencyRate) {
        this.consistencyRate = consistencyRate;
    }

    public long getActiveHabitsCount() {
        return activeHabitsCount;
    }

    public void setActiveHabitsCount(long activeHabitsCount) {
        this.activeHabitsCount = activeHabitsCount;
    }

    public long getTotalHabitsCount() {
        return totalHabitsCount;
    }

    public void setTotalHabitsCount(long totalHabitsCount) {
        this.totalHabitsCount = totalHabitsCount;
    }
}
