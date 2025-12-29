-- Settlement Service Database Schema
-- This file contains DDL statements for creating the settlement_db schema

-- Drop table if exists (for clean recreate)
DROP TABLE IF EXISTS settlement;

-- Create settlement table
CREATE TABLE settlement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payer_id BIGINT NOT NULL COMMENT 'User ID who is making the payment',
    payee_id BIGINT NOT NULL COMMENT 'User ID who is receiving the payment',
    group_id BIGINT COMMENT 'Optional group ID for group-specific settlements',
    amount DECIMAL(19, 2) NOT NULL COMMENT 'Amount being settled',
    settlement_date DATETIME COMMENT 'Date and time when the settlement occurred',
    status VARCHAR(20) NOT NULL COMMENT 'Status of the settlement: PENDING, COMPLETED, CANCELLED',
    notes TEXT COMMENT 'Optional notes about the settlement',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the record was created',
    
    -- Indexes for performance
    INDEX idx_payer (payer_id),
    INDEX idx_payee (payee_id),
    INDEX idx_group (group_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Table storing settlements (payments) between users';
