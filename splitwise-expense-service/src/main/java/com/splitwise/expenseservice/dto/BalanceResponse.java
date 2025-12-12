package com.splitwise.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for balance information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    
    private Long userId;
    private Long groupId;
    private BigDecimal totalOwed;
    private BigDecimal totalPaid;
    private BigDecimal netBalance; // totalOwed - totalPaid (positive = owes money, negative = is owed money)
    private BigDecimal outstandingBalance; // amount still owed (totalOwed - totalPaid, only if > 0)
}

