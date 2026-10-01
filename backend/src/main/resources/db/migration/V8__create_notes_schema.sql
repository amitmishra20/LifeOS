-- ==============================================================================
-- LifeOS Flyway Migration: V8__create_notes_schema.sql
-- ==============================================================================
-- Phase: Phase 10 — Notes
-- Description: Creates notes table with user ownership, timestamps, and indexes.
-- ==============================================================================

CREATE TABLE notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NULL,
    category VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_notes_user ON notes (user_id);
CREATE INDEX idx_notes_user_category ON notes (user_id, category);
