-- =========================================================================
-- LifeOS V6: Learning Module Schema
-- Creates learning_items and learning_sessions tables
-- =========================================================================

CREATE TABLE learning_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    goal_id BIGINT NULL,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(500) NULL,
    category VARCHAR(30) NOT NULL DEFAULT 'TECHNICAL',
    target_progress INT NOT NULL DEFAULT 100,
    current_progress INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_learning_items_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_learning_items_goal FOREIGN KEY (goal_id) REFERENCES goals(id) ON DELETE SET NULL,
    CONSTRAINT chk_learning_progress CHECK (current_progress >= 0 AND current_progress <= 100),
    CONSTRAINT chk_learning_target CHECK (target_progress >= 1 AND target_progress <= 100)
);

CREATE INDEX idx_learning_items_user ON learning_items(user_id);
CREATE INDEX idx_learning_items_user_status ON learning_items(user_id, status);
CREATE INDEX idx_learning_items_goal ON learning_items(goal_id);

CREATE TABLE learning_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learning_item_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    duration_minutes INT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_learning_sessions_item FOREIGN KEY (learning_item_id) REFERENCES learning_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_learning_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_session_duration CHECK (duration_minutes > 0)
);

CREATE INDEX idx_learning_sessions_item ON learning_sessions(learning_item_id);
CREATE INDEX idx_learning_sessions_user ON learning_sessions(user_id);
CREATE INDEX idx_learning_sessions_item_date ON learning_sessions(learning_item_id, session_date DESC);
CREATE INDEX idx_learning_sessions_user_date ON learning_sessions(user_id, session_date DESC);
