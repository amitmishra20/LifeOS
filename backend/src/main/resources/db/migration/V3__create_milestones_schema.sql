-- ==============================================================================
-- LifeOS Flyway Migration: V3__create_milestones_schema.sql
-- ==============================================================================
-- Phase: Phase 5 — Goals + Milestones + Life Map
-- Description: Creates milestones table with foreign keys, ordering, and indexes.
-- ==============================================================================

CREATE TABLE milestones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    progress INT NOT NULL DEFAULT 0,
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_milestones_goal FOREIGN KEY (goal_id) REFERENCES goals (id) ON DELETE CASCADE,
    CONSTRAINT fk_milestones_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_milestones_goal_id (goal_id),
    INDEX idx_milestones_user_id (user_id),
    INDEX idx_milestones_goal_order (goal_id, order_index)
);
