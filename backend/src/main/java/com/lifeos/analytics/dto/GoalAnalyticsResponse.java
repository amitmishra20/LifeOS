package com.lifeos.analytics.dto;

public class GoalAnalyticsResponse {

    private double progressPercentage;
    private long totalGoals;
    private long activeGoals;
    private long completedGoals;
    private long totalMilestones;
    private long completedMilestones;

    public GoalAnalyticsResponse() {
    }

    public GoalAnalyticsResponse(double progressPercentage, long totalGoals, long activeGoals, long completedGoals, long totalMilestones, long completedMilestones) {
        this.progressPercentage = progressPercentage;
        this.totalGoals = totalGoals;
        this.activeGoals = activeGoals;
        this.completedGoals = completedGoals;
        this.totalMilestones = totalMilestones;
        this.completedMilestones = completedMilestones;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public long getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(long totalGoals) {
        this.totalGoals = totalGoals;
    }

    public long getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(long activeGoals) {
        this.activeGoals = activeGoals;
    }

    public long getCompletedGoals() {
        return completedGoals;
    }

    public void setCompletedGoals(long completedGoals) {
        this.completedGoals = completedGoals;
    }

    public long getTotalMilestones() {
        return totalMilestones;
    }

    public void setTotalMilestones(long totalMilestones) {
        this.totalMilestones = totalMilestones;
    }

    public long getCompletedMilestones() {
        return completedMilestones;
    }

    public void setCompletedMilestones(long completedMilestones) {
        this.completedMilestones = completedMilestones;
    }
}
