package com.lifeos.goal.dto;

import com.lifeos.goal.entity.Goal;
import com.lifeos.goal.entity.GoalCategory;
import com.lifeos.goal.entity.GoalPriority;
import com.lifeos.goal.entity.GoalStatus;

import java.time.Instant;
import java.time.LocalDate;

public class GoalResponse {

    private Long id;
    private String title;
    private String description;
    private GoalCategory category;
    private GoalPriority priority;
    private GoalStatus status;
    private Integer progress;
    private LocalDate startDate;
    private LocalDate targetDate;
    private int milestoneCount;
    private int completedMilestoneCount;
    private Instant createdAt;
    private Instant updatedAt;

    public GoalResponse() {
    }

    public static GoalResponse fromEntity(Goal goal) {
        GoalResponse response = new GoalResponse();
        response.setId(goal.getId());
        response.setTitle(goal.getTitle());
        response.setDescription(goal.getDescription());
        response.setCategory(goal.getCategory());
        response.setPriority(goal.getPriority());
        response.setStatus(goal.getStatus());
        response.setProgress(goal.getProgress());
        response.setStartDate(goal.getStartDate());
        response.setTargetDate(goal.getTargetDate());
        if (goal.getMilestones() != null) {
            response.setMilestoneCount(goal.getMilestones().size());
            response.setCompletedMilestoneCount(
                    (int) goal.getMilestones().stream()
                            .filter(m -> m.getStatus() == com.lifeos.milestone.entity.MilestoneStatus.COMPLETED)
                            .count()
            );
        }
        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GoalCategory getCategory() {
        return category;
    }

    public void setCategory(GoalCategory category) {
        this.category = category;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
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

    public int getMilestoneCount() {
        return milestoneCount;
    }

    public void setMilestoneCount(int milestoneCount) {
        this.milestoneCount = milestoneCount;
    }

    public int getCompletedMilestoneCount() {
        return completedMilestoneCount;
    }

    public void setCompletedMilestoneCount(int completedMilestoneCount) {
        this.completedMilestoneCount = completedMilestoneCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
