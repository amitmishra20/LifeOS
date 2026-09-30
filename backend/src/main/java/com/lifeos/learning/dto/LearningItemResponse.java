package com.lifeos.learning.dto;

import com.lifeos.learning.entity.LearningCategory;
import com.lifeos.learning.entity.LearningItem;
import com.lifeos.learning.entity.LearningStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LearningItemResponse {

    private Long id;
    private String title;
    private String description;
    private LearningCategory category;
    private Integer targetProgress;
    private Integer currentProgress;
    private LearningStatus status;
    private Long goalId;
    private String goalTitle;
    private Integer totalMinutesLearned;
    private Double totalHoursLearned;
    private Long sessionCount;
    private List<LearningSessionResponse> sessions = new ArrayList<>();
    private List<LearningSessionResponse> recentSessions = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public LearningItemResponse() {
    }

    public static LearningItemResponse fromEntity(LearningItem item, Integer totalMinutes, Long sessionCount, List<LearningSessionResponse> sessionList) {
        LearningItemResponse response = new LearningItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setCategory(item.getCategory());
        response.setTargetProgress(item.getTargetProgress());
        response.setCurrentProgress(item.getCurrentProgress());
        response.setStatus(item.getStatus());
        if (item.getGoal() != null) {
            response.setGoalId(item.getGoal().getId());
            response.setGoalTitle(item.getGoal().getTitle());
        }
        int minutes = totalMinutes != null ? totalMinutes : 0;
        response.setTotalMinutesLearned(minutes);
        response.setTotalHoursLearned(Math.round((minutes / 60.0) * 10.0) / 10.0);
        response.setSessionCount(sessionCount != null ? sessionCount : 0L);
        List<LearningSessionResponse> list = sessionList != null ? sessionList : new ArrayList<>();
        response.setSessions(list);
        response.setRecentSessions(list);
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
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

    public LearningCategory getCategory() {
        return category;
    }

    public void setCategory(LearningCategory category) {
        this.category = category;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(Integer targetProgress) {
        this.targetProgress = targetProgress;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public LearningStatus getStatus() {
        return status;
    }

    public void setStatus(LearningStatus status) {
        this.status = status;
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

    public Integer getTotalMinutesLearned() {
        return totalMinutesLearned;
    }

    public void setTotalMinutesLearned(Integer totalMinutesLearned) {
        this.totalMinutesLearned = totalMinutesLearned;
    }

    public Double getTotalHoursLearned() {
        return totalHoursLearned;
    }

    public void setTotalHoursLearned(Double totalHoursLearned) {
        this.totalHoursLearned = totalHoursLearned;
    }

    public Long getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Long sessionCount) {
        this.sessionCount = sessionCount;
    }

    public List<LearningSessionResponse> getSessions() {
        return sessions;
    }

    public void setSessions(List<LearningSessionResponse> sessions) {
        this.sessions = sessions;
    }

    public List<LearningSessionResponse> getRecentSessions() {
        return recentSessions;
    }

    public void setRecentSessions(List<LearningSessionResponse> recentSessions) {
        this.recentSessions = recentSessions;
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
