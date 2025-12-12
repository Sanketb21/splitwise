package com.splitwise.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for expense participant response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseParticipantResponse {
    
    private Long id;
    private Long expenseId;
    private Long userId;
    private BigDecimal amountOwed;
    private BigDecimal amountPaid;
    private LocalDateTime createdAt;
}

