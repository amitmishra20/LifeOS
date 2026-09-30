package com.lifeos.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateLearningSessionRequest {

    @NotNull(message = "Session date is required")
    @PastOrPresent(message = "Session date cannot be in the future")
    private LocalDate sessionDate;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotBlank(message = "Topic is required")
    @Size(max = 255, message = "Topic must not exceed 255 characters")
    private String topic;

    private String notes;

    @Min(value = 0, message = "Progress must be between 0 and 100")
    @Max(value = 100, message = "Progress must be between 0 and 100")
    private Integer newProgress;

    public CreateLearningSessionRequest() {
    }

    public CreateLearningSessionRequest(LocalDate sessionDate, Integer durationMinutes, String topic, String notes) {
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.topic = topic;
        this.notes = notes;
    }

    public CreateLearningSessionRequest(LocalDate sessionDate, Integer durationMinutes, String topic, String notes, Integer newProgress) {
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.topic = topic;
        this.notes = notes;
        this.newProgress = newProgress;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getNewProgress() {
        return newProgress;
    }

    public void setNewProgress(Integer newProgress) {
        this.newProgress = newProgress;
    }
}
