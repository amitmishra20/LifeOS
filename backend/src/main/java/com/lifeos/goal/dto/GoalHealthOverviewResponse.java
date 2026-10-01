package com.lifeos.goal.dto;

import java.util.Collections;
import java.util.List;

public class GoalHealthOverviewResponse {

    private int totalGoals;
    private int onTrackCount;
    private int atRiskCount;
    private int behindCount;
    private int completedCount;
    private List<GoalHealthEvaluationDto> evaluations = Collections.emptyList();

    public GoalHealthOverviewResponse() {
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getOnTrackCount() {
        return onTrackCount;
    }

    public void setOnTrackCount(int onTrackCount) {
        this.onTrackCount = onTrackCount;
    }

    public int getAtRiskCount() {
        return atRiskCount;
    }

    public void setAtRiskCount(int atRiskCount) {
        this.atRiskCount = atRiskCount;
    }

    public int getBehindCount() {
        return behindCount;
    }

    public void setBehindCount(int behindCount) {
        this.behindCount = behindCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public List<GoalHealthEvaluationDto> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<GoalHealthEvaluationDto> evaluations) {
        this.evaluations = evaluations != null ? evaluations : Collections.emptyList();
    }
}
