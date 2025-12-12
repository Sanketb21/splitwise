package com.splitwise.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for expense response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    
    private Long id;
    private Long groupId;
    private Long paidBy;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private String splitType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ExpenseParticipantResponse> participants;
}

