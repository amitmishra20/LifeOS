package com.lifeos.habit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class HabitDayHistoryDto {

    private LocalDate date;
    private String dayOfWeek;

    @JsonProperty("isScheduled")
    private boolean isScheduled;

    @JsonProperty("isCompleted")
    private boolean isCompleted;

    @JsonProperty("isPaused")
    private boolean isPaused;

    @JsonProperty("isToday")
    private boolean isToday;

    public HabitDayHistoryDto() {
    }

    public HabitDayHistoryDto(LocalDate date, String dayOfWeek, boolean isScheduled, boolean isCompleted, boolean isPaused, boolean isToday) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.isScheduled = isScheduled;
        this.isCompleted = isCompleted;
        this.isPaused = isPaused;
        this.isToday = isToday;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}
