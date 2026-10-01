package com.lifeos.user.dto;

import java.time.Instant;

public class UserProfileSummaryResponse {

    private Long id;
    private String name;
    private String email;
    private Instant createdAt;
    private long totalGoals;
    private long totalTasks;
    private long totalHabits;
    private long totalLearningItems;
    private long totalNotes;

    public UserProfileSummaryResponse() {
    }

    public UserProfileSummaryResponse(
            Long id,
            String name,
            String email,
            Instant createdAt,
            long totalGoals,
            long totalTasks,
            long totalHabits,
            long totalLearningItems,
            long totalNotes
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.totalGoals = totalGoals;
        this.totalTasks = totalTasks;
        this.totalHabits = totalHabits;
        this.totalLearningItems = totalLearningItems;
        this.totalNotes = totalNotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public long getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(long totalGoals) {
        this.totalGoals = totalGoals;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getTotalHabits() {
        return totalHabits;
    }

    public void setTotalHabits(long totalHabits) {
        this.totalHabits = totalHabits;
    }

    public long getTotalLearningItems() {
        return totalLearningItems;
    }

    public void setTotalLearningItems(long totalLearningItems) {
        this.totalLearningItems = totalLearningItems;
    }

    public long getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(long totalNotes) {
        this.totalNotes = totalNotes;
    }
}
