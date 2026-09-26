package com.lifeos.recommendation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lifeos.focus.scoring")
public class FocusScoringProperties {

    private int priorityLow = 10;
    private int priorityMedium = 20;
    private int priorityHigh = 30;
    private int priorityCritical = 40;

    private int overdue = 50;
    private int dueToday = 40;
    private int dueTomorrow = 30;
    private int dueIn2Days = 20;
    private int dueIn3To7Days = 10;
    private int dueBeyond7Days = 0;

    private int goalBehind = 30;
    private int goalAtRisk = 15;
    private int goalOnTrack = 0;

    private int maxRecommendations = 5;

    public int getPriorityLow() {
        return priorityLow;
    }

    public void setPriorityLow(int priorityLow) {
        this.priorityLow = priorityLow;
    }

    public int getPriorityMedium() {
        return priorityMedium;
    }

    public void setPriorityMedium(int priorityMedium) {
        this.priorityMedium = priorityMedium;
    }

    public int getPriorityHigh() {
        return priorityHigh;
    }

    public void setPriorityHigh(int priorityHigh) {
        this.priorityHigh = priorityHigh;
    }

    public int getPriorityCritical() {
        return priorityCritical;
    }

    public void setPriorityCritical(int priorityCritical) {
        this.priorityCritical = priorityCritical;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public int getDueToday() {
        return dueToday;
    }

    public void setDueToday(int dueToday) {
        this.dueToday = dueToday;
    }

    public int getDueTomorrow() {
        return dueTomorrow;
    }

    public void setDueTomorrow(int dueTomorrow) {
        this.dueTomorrow = dueTomorrow;
    }

    public int getDueIn2Days() {
        return dueIn2Days;
    }

    public void setDueIn2Days(int dueIn2Days) {
        this.dueIn2Days = dueIn2Days;
    }

    public int getDueIn3To7Days() {
        return dueIn3To7Days;
    }

    public void setDueIn3To7Days(int dueIn3To7Days) {
        this.dueIn3To7Days = dueIn3To7Days;
    }

    public int getDueBeyond7Days() {
        return dueBeyond7Days;
    }

    public void setDueBeyond7Days(int dueBeyond7Days) {
        this.dueBeyond7Days = dueBeyond7Days;
    }

    public int getGoalBehind() {
        return goalBehind;
    }

    public void setGoalBehind(int goalBehind) {
        this.goalBehind = goalBehind;
    }

    public int getGoalAtRisk() {
        return goalAtRisk;
    }

    public void setGoalAtRisk(int goalAtRisk) {
        this.goalAtRisk = goalAtRisk;
    }

    public int getGoalOnTrack() {
        return goalOnTrack;
    }

    public void setGoalOnTrack(int goalOnTrack) {
        this.goalOnTrack = goalOnTrack;
    }

    public int getMaxRecommendations() {
        return maxRecommendations;
    }

    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }
}
