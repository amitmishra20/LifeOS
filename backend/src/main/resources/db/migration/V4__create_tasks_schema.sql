-- ==============================================================================
-- LifeOS Flyway Migration: V4__create_tasks_schema.sql
-- ==============================================================================
-- Phase: Phase 6 — Tasks + Today's Focus
-- Description: Creates tasks table with foreign keys, status/priority, and indexes.
-- ==============================================================================

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NULL,
    milestone_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    due_date DATE,
    estimated_minutes INT,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_goal FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE SET NULL,
    CONSTRAINT fk_tasks_milestone FOREIGN KEY (milestone_id) REFERENCES milestones (id) ON DELETE SET NULL,
    INDEX idx_tasks_user_status (user_id, status),
    INDEX idx_tasks_user_due_date (user_id, due_date),
    INDEX idx_tasks_goal_id (goal_id),
    INDEX idx_tasks_milestone_id (milestone_id)
);
