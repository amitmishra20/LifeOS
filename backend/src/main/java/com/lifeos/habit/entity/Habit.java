package com.lifeos.habit.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.goal.entity.Goal;
import com.lifeos.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "habits")
public class Habit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type", nullable = false, length = 50)
    private HabitFrequencyType frequencyType;

    @Column(name = "target_days_mask", length = 50)
    private String targetDaysMask;

    @Column(name = "target_per_week", nullable = false)
    private Integer targetPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HabitStatus status = HabitStatus.ACTIVE;

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "icon", length = 50)
    private String icon;

    public Habit() {
    }

    public Habit(User user, String title, HabitFrequencyType frequencyType, Integer targetPerWeek) {
        this.user = user;
        this.title = title;
        this.frequencyType = frequencyType;
        this.targetPerWeek = targetPerWeek;
        this.status = HabitStatus.ACTIVE;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
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
}
