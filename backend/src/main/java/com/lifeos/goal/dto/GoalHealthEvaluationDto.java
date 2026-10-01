package com.lifeos.goal.dto;

import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalHealth;
import com.lifeos.goal.entity.GoalStatus;

import java.time.LocalDate;

public class GoalHealthEvaluationDto {

    private Long goalId;
    private String goalTitle;
    private GoalCategory category;
    private GoalStatus status;
    private GoalHealth health;
    private Double actualProgress;
    private Double expectedProgress;
    private Double delta;
    private LocalDate startDate;
    private LocalDate targetDate;
    private LocalDate effectiveStartDate;
    private Long totalDays;
    private Long elapsedDays;
    private Long remainingDays;
    private String reason;

    public GoalHealthEvaluationDto() {
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public GoalCategory getCategory() {
        return category;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public GoalHealth getHealth() {
        return health;
    }

    public void setHealth(GoalHealth health) {
        this.health = health;
    }

    public Double getActualProgress() {
        return actualProgress;
    }

    public void setActualProgress(Double actualProgress) {
        this.actualProgress = actualProgress;
    }

    public Double getExpectedProgress() {
        return expectedProgress;
    }

    public void setExpectedProgress(Double expectedProgress) {
        this.expectedProgress = expectedProgress;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Long totalDays) {
        this.totalDays = totalDays;
    }

    public Long getElapsedDays() {
        return elapsedDays;
    }

    public void setElapsedDays(Long elapsedDays) {
        this.elapsedDays = elapsedDays;
    }

    public Long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Long remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
