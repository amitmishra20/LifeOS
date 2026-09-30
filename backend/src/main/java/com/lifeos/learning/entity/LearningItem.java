package com.lifeos.learning.entity;

import com.lifeos.common.entity.BaseEntity;
import com.lifeos.goal.entity.Goal;
import com.lifeos.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_items")
public class LearningItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LearningCategory category = LearningCategory.TECHNICAL;

    @Column(name = "target_progress", nullable = false)
    private Integer targetProgress = 100;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LearningStatus status = LearningStatus.ACTIVE;

    @OneToMany(mappedBy = "learningItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningSession> sessions = new ArrayList<>();

    public LearningItem() {
    }

    public LearningItem(User user, String title, LearningCategory category, Integer targetProgress) {
        this.user = user;
        this.title = title;
        this.category = category != null ? category : LearningCategory.TECHNICAL;
        this.targetProgress = targetProgress != null ? targetProgress : 100;
        this.currentProgress = 0;
        this.status = LearningStatus.ACTIVE;
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

    public LearningCategory getCategory() {
        return category;
    }

    public void setCategory(LearningCategory category) {
        this.category = category;
    }

    public Integer getTargetProgress() {
        return targetProgress;
    }

    public void setTargetProgress(Integer targetProgress) {
        this.targetProgress = targetProgress;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public LearningStatus getStatus() {
        return status;
    }

    public void setStatus(LearningStatus status) {
        this.status = status;
    }

    public List<LearningSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<LearningSession> sessions) {
        this.sessions = sessions;
    }
}
