package com.lifeos.task.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.goal.entity.Goal;
import com.lifeos.milestone.entity.Milestone;
import com.lifeos.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Task() {
    }

    public Task(User user, String title) {
        this.user = user;
        this.title = title;
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
    }

    public boolean isOverdue() {
        return status != TaskStatus.COMPLETED && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public TaskStatus getEffectiveStatus() {
        if (this.status == TaskStatus.COMPLETED) {
            return TaskStatus.COMPLETED;
        }
        if (this.dueDate != null && this.dueDate.isBefore(LocalDate.now())) {
            return TaskStatus.OVERDUE;
        }
        if (this.status == TaskStatus.OVERDUE) {
            return TaskStatus.TODO;
        }
        return this.status;
    }

    @PrePersist
    @PreUpdate
    public void syncLifecycleStatus() {
        if (this.status != TaskStatus.COMPLETED) {
            if (this.dueDate != null && this.dueDate.isBefore(LocalDate.now())) {
                this.status = TaskStatus.OVERDUE;
            } else if (this.status == TaskStatus.OVERDUE) {
                this.status = TaskStatus.TODO;
            }
        }
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

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status != null ? status : TaskStatus.TODO;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
