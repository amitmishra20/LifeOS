package com.lifeos.recommendation.dto;

import com.lifeos.task.dto.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class FocusItemResponse {

    private TaskResponse task;
    private int totalScore;
    private int priorityScore;
    private int deadlineScore;
    private int goalHealthScore;
    private String primaryReason;
    private List<String> reasons = new ArrayList<>();

    public FocusItemResponse() {
    }

    public TaskResponse getTask() {
        return task;
    }

    public void setTask(TaskResponse task) {
        this.task = task;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public int getDeadlineScore() {
        return deadlineScore;
    }

    public void setDeadlineScore(int deadlineScore) {
        this.deadlineScore = deadlineScore;
    }

    public int getGoalHealthScore() {
        return goalHealthScore;
    }

    public void setGoalHealthScore(int goalHealthScore) {
        this.goalHealthScore = goalHealthScore;
    }

    public String getPrimaryReason() {
        return primaryReason;
    }

    public void setPrimaryReason(String primaryReason) {
        this.primaryReason = primaryReason;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
