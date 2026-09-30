package com.lifeos.habit.dto;

import com.lifeos.habit.entity.HabitLog;

import java.time.Instant;
import java.time.LocalDate;

public class HabitLogResponse {

    private Long id;
    private Long habitId;
    private Long userId;
    private LocalDate completionDate;
    private String notes;
    private Instant createdAt;

    public HabitLogResponse() {
    }

    public HabitLogResponse(Long id, Long habitId, Long userId, LocalDate completionDate, String notes, Instant createdAt) {
        this.id = id;
        this.habitId = habitId;
        this.userId = userId;
        this.completionDate = completionDate;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static HabitLogResponse fromEntity(HabitLog log) {
        if (log == null) return null;
        return new HabitLogResponse(
                log.getId(),
                log.getHabit() != null ? log.getHabit().getId() : null,
                log.getUser() != null ? log.getUser().getId() : null,
                log.getCompletionDate(),
                log.getNotes(),
                log.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
