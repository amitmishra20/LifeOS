package com.lifeos.habit.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
    name = "habit_logs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_habit_logs_habit_date", columnNames = {"habit_id", "completion_date"})
    }
)
public class HabitLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public HabitLog() {
    }

    public HabitLog(Habit habit, User user, LocalDate completionDate) {
        this.habit = habit;
        this.user = user;
        this.completionDate = completionDate;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
