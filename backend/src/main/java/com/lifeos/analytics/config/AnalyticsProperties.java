package com.lifeos.analytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.analytics.weights")
public class AnalyticsProperties {

    private double taskCompletion = 0.30;
    private double habitConsistency = 0.25;
    private double goalProgress = 0.25;
    private double learningActivity = 0.20;

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
