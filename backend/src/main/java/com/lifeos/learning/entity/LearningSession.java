package com.lifeos.learning.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "learning_sessions")
public class LearningSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learning_item_id", nullable = false)
    private LearningItem learningItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, length = 255)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public LearningSession() {
    }

    public LearningSession(LearningItem learningItem, User user, LocalDate sessionDate, Integer durationMinutes, String topic, String notes) {
        this.learningItem = learningItem;
        this.user = user;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.topic = topic;
        this.notes = notes;
    }

    public LearningItem getLearningItem() {
        return learningItem;
    }

    public void setLearningItem(LearningItem learningItem) {
        this.learningItem = learningItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
