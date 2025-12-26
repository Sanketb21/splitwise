package com.splitwise.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for simplified debt response
 * Represents a single optimized transaction to settle debts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedDebtResponse {

    /**
     * User ID who needs to pay
     */
    private Long payerId;

    /**
     * User ID who needs to receive the payment
     */
    private Long payeeId;

    /**
     * Amount to be transferred from payer to payee
     */
    private BigDecimal amount;

    /**
     * Group ID (context for the debt simplification)
     */
    private Long groupId;
}
