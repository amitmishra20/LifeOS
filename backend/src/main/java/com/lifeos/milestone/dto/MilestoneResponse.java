package com.lifeos.milestone.dto;

import com.lifeos.milestone.entity.Milestone;
import com.lifeos.milestone.entity.MilestoneStatus;

import java.time.Instant;
import java.time.LocalDate;

public class MilestoneResponse {

    private Long id;
    private Long goalId;
    private String title;
    private String description;
    private LocalDate targetDate;
    private MilestoneStatus status;
    private Integer progress;
    private Integer orderIndex;
    private Instant createdAt;
    private Instant updatedAt;

    public MilestoneResponse() {
    }

    public static MilestoneResponse fromEntity(Milestone milestone) {
        MilestoneResponse response = new MilestoneResponse();
        response.setId(milestone.getId());
        response.setGoalId(milestone.getGoal() != null ? milestone.getGoal().getId() : null);
        response.setTitle(milestone.getTitle());
        response.setDescription(milestone.getDescription());
        response.setTargetDate(milestone.getTargetDate());
        response.setStatus(milestone.getStatus());
        response.setProgress(milestone.getProgress());
        response.setOrderIndex(milestone.getOrderIndex());
        response.setCreatedAt(milestone.getCreatedAt());
        response.setUpdatedAt(milestone.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
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

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public MilestoneStatus getStatus() {
        return status;
    }

    public void setStatus(MilestoneStatus status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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
