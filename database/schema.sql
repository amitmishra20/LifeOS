-- ==============================================================================
-- LifeOS Database Initialization Script
-- ==============================================================================
-- Description: Minimal database creation script for LifeOS.
-- Note: All table schemas and versioned migrations are managed authoritatively
--       by Flyway (backend/src/main/resources/db/migration/).
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS lifeos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lifeos;

-- Baseline check
SELECT 'LifeOS database initialized successfully.' AS status;
