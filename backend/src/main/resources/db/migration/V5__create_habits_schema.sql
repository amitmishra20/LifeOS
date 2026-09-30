-- ==============================================================================
-- LifeOS Flyway Migration: V5__create_habits_schema.sql
-- ==============================================================================
-- Phase: Phase 7 — Habits + Habit Logs + Cadence Rhythm
-- Description: Creates habits, habit_logs, and habit_pause_intervals tables with
--              foreign keys, constraints, and performance indexes.
-- ==============================================================================

-- Habits Table
CREATE TABLE habits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    frequency_type VARCHAR(50) NOT NULL,
    target_days_mask VARCHAR(50) NULL,
    target_per_week INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    paused_at TIMESTAMP NULL,
    icon VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_habits_goal FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE SET NULL,
    INDEX idx_habits_user_id (user_id),
    INDEX idx_habits_user_status (user_id, status),
    INDEX idx_habits_goal_id (goal_id)
);

-- Habit Logs Table
CREATE TABLE habit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    habit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    completion_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_habit_logs_habit FOREIGN KEY (habit_id) REFERENCES habits (id) ON DELETE CASCADE,
    CONSTRAINT fk_habit_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_habit_logs_habit_date UNIQUE (habit_id, completion_date),
    INDEX idx_habit_logs_habit_id (habit_id),
    INDEX idx_habit_logs_user_date (user_id, completion_date)
);

-- Habit Pause Intervals Table
CREATE TABLE habit_pause_intervals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    habit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    paused_at TIMESTAMP NOT NULL,
    resumed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_habit_pauses_habit FOREIGN KEY (habit_id) REFERENCES habits (id) ON DELETE CASCADE,
    CONSTRAINT fk_habit_pauses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_habit_pauses_habit (habit_id),
    INDEX idx_habit_pauses_user (user_id)
);
