package com.lifeos.analytics.dto;

public class ProductivityWeightsDto {

    private double taskCompletion;
    private double habitConsistency;
    private double goalProgress;
    private double learningActivity;

    public ProductivityWeightsDto() {
    }

    public ProductivityWeightsDto(double taskCompletion, double habitConsistency, double goalProgress, double learningActivity) {
        this.taskCompletion = taskCompletion;
        this.habitConsistency = habitConsistency;
        this.goalProgress = goalProgress;
        this.learningActivity = learningActivity;
    }

    public double getTaskCompletion() {
        return taskCompletion;
    }

    public void setTaskCompletion(double taskCompletion) {
        this.taskCompletion = taskCompletion;
    }

    public double getHabitConsistency() {
        return habitConsistency;
    }

    public void setHabitConsistency(double habitConsistency) {
        this.habitConsistency = habitConsistency;
    }

    public double getGoalProgress() {
        return goalProgress;
    }

    public void setGoalProgress(double goalProgress) {
        this.goalProgress = goalProgress;
    }

    public double getLearningActivity() {
        return learningActivity;
    }

    public void setLearningActivity(double learningActivity) {
        this.learningActivity = learningActivity;
    }
}
