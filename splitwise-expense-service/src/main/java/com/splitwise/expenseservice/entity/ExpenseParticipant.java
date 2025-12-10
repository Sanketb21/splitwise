package com.splitwise.expenseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ExpenseParticipant Entity
 * 
 * Represents a participant in an expense and their share
 */
@Entity
@Table(name = "expense_participants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_id", nullable = false)
    private Long expenseId; // Foreign key to Expense

    @Column(name = "user_id", nullable = false)
    private Long userId; // Foreign key to User (in user-service)

    @Column(name = "amount_owed", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountOwed; // How much this user owes for this expense

    @Column(name = "amount_paid", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO; // How much this user has paid (default 0)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (amountPaid == null) {
            amountPaid = BigDecimal.ZERO;
        }
    }
}

