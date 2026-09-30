package com.lifeos.learning.dto;

import com.lifeos.learning.entity.LearningSession;

import java.time.LocalDate;
import java.time.Instant;

public class LearningSessionResponse {

    private Long id;
    private Long learningItemId;
    private LocalDate sessionDate;
    private Integer durationMinutes;
    private String topic;
    private String notes;
    private Instant createdAt;

    public LearningSessionResponse() {
    }

    public static LearningSessionResponse fromEntity(LearningSession session) {
        LearningSessionResponse response = new LearningSessionResponse();
        response.setId(session.getId());
        response.setLearningItemId(session.getLearningItem() != null ? session.getLearningItem().getId() : null);
        response.setSessionDate(session.getSessionDate());
        response.setDurationMinutes(session.getDurationMinutes());
        response.setTopic(session.getTopic());
        response.setNotes(session.getNotes());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLearningItemId() {
        return learningItemId;
    }

    public void setLearningItemId(Long learningItemId) {
        this.learningItemId = learningItemId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
