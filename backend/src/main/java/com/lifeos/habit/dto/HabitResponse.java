package com.lifeos.habit.dto;

import com.lifeos.habit.entity.HabitFrequencyType;
import com.lifeos.habit.entity.HabitStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HabitResponse {

    private Long id;
    private Long userId;
    private Long goalId;
    private String goalTitle;
    private String title;
    private String description;
    private HabitFrequencyType frequencyType;
    private String targetDaysMask;
    private Integer targetPerWeek;
    private HabitStatus status;
    private Instant pausedAt;
    private String icon;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean completedToday;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer consistencyRate;
    private Integer weeklyTargetProgress;
    private Integer weeklyTargetRemaining;
    private List<HabitDayHistoryDto> history = new ArrayList<>();

    public HabitResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public HabitFrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(HabitFrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public String getTargetDaysMask() {
        return targetDaysMask;
    }

    public void setTargetDaysMask(String targetDaysMask) {
        this.targetDaysMask = targetDaysMask;
    }

    public Integer getTargetPerWeek() {
        return targetPerWeek;
    }

    public void setTargetPerWeek(Integer targetPerWeek) {
        this.targetPerWeek = targetPerWeek;
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }

    public Instant getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(Instant pausedAt) {
        this.pausedAt = pausedAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public boolean isCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(Integer longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Integer getConsistencyRate() {
        return consistencyRate;
    }

    public void setConsistencyRate(Integer consistencyRate) {
        this.consistencyRate = consistencyRate;
    }

    public Integer getWeeklyTargetProgress() {
        return weeklyTargetProgress;
    }

    public void setWeeklyTargetProgress(Integer weeklyTargetProgress) {
        this.weeklyTargetProgress = weeklyTargetProgress;
    }

    public Integer getWeeklyTargetRemaining() {
        return weeklyTargetRemaining;
    }

    public void setWeeklyTargetRemaining(Integer weeklyTargetRemaining) {
        this.weeklyTargetRemaining = weeklyTargetRemaining;
    }

    public List<HabitDayHistoryDto> getHistory() {
        return history;
    }

    public void setHistory(List<HabitDayHistoryDto> history) {
        this.history = history;
    }
}
