package com.lifeos.habit.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "habit_pause_intervals")
public class HabitPauseInterval extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "paused_at", nullable = false)
    private Instant pausedAt;

    @Column(name = "resumed_at")
    private Instant resumedAt;

    public HabitPauseInterval() {
    }

    public HabitPauseInterval(Habit habit, User user, Instant pausedAt) {
        this.habit = habit;
        this.user = user;
        this.pausedAt = pausedAt;
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

    public Instant getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(Instant pausedAt) {
        this.pausedAt = pausedAt;
    }

    public Instant getResumedAt() {
        return resumedAt;
    }

    public void setResumedAt(Instant resumedAt) {
        this.resumedAt = resumedAt;
    }
}
