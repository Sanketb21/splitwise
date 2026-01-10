-- Notification Service Database Schema
-- This file contains DDL statements for creating the notification_db schema

-- Drop table if exists (for clean recreate)
DROP TABLE IF EXISTS notification;

-- Create notification table
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'User ID who receives the notification',
    type VARCHAR(50) NOT NULL COMMENT 'Type of notification: EXPENSE_ADDED, SETTLEMENT, GROUP_INVITATION, etc.',
    title VARCHAR(255) NOT NULL COMMENT 'Notification title',
    message TEXT NOT NULL COMMENT 'Notification message content',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether the notification has been read',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the notification was created',
    
    -- Indexes for performance
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table storing notifications for users';
