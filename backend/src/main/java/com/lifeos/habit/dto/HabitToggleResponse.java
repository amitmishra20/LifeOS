package com.lifeos.habit.dto;

import java.time.LocalDate;

public class HabitToggleResponse {

    private Long habitId;
    private LocalDate completionDate;
    private boolean completed;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer consistencyRate;
    private Integer weeklyTargetProgress;
    private Integer weeklyTargetRemaining;
    private HabitResponse habit;

    public HabitToggleResponse() {
    }

    public HabitToggleResponse(Long habitId, LocalDate completionDate, boolean completed,
                               Integer currentStreak, Integer longestStreak, Integer consistencyRate,
                               Integer weeklyTargetProgress, Integer weeklyTargetRemaining,
                               HabitResponse habit) {
        this.habitId = habitId;
        this.completionDate = completionDate;
        this.completed = completed;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.consistencyRate = consistencyRate;
        this.weeklyTargetProgress = weeklyTargetProgress;
        this.weeklyTargetRemaining = weeklyTargetRemaining;
        this.habit = habit;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public HabitResponse getHabit() {
        return habit;
    }

    public void setHabit(HabitResponse habit) {
        this.habit = habit;
    }
}
