-- Create database for Expense Service
CREATE DATABASE IF NOT EXISTS expense_db;

USE expense_db;

-- Expense Table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    paid_by BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    description TEXT,
    expense_date DATE NOT NULL,
    split_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_group_id (group_id),
    INDEX idx_paid_by (paid_by),
    INDEX idx_expense_date (expense_date)
);

-- ExpenseParticipant Table
CREATE TABLE IF NOT EXISTS expense_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount_owed DECIMAL(19, 2) NOT NULL,
    amount_paid DECIMAL(19, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    INDEX idx_expense_id (expense_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY unique_expense_user (expense_id, user_id)
);

