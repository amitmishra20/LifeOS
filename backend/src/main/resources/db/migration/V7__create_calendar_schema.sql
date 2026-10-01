-- ==============================================================================
-- LifeOS Flyway Migration: V7__create_calendar_schema.sql
-- ==============================================================================
-- Phase: Phase 9 — Calendar
-- Description: Creates calendar_events table with user ownership, timestamps, and indexes.
-- ==============================================================================

CREATE TABLE calendar_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NULL,
    event_type VARCHAR(50) NOT NULL DEFAULT 'CUSTOM_EVENT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_calendar_events_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_calendar_events_user ON calendar_events (user_id);
CREATE INDEX idx_calendar_events_user_start_time ON calendar_events (user_id, start_time);
CREATE INDEX idx_calendar_events_user_type ON calendar_events (user_id, event_type);
