package com.splitwise.expenseservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for expense participant in request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseParticipantRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // For EQUAL split: this will be calculated, can be null
    // For UNEQUAL split: this is the custom amount
    // For PERCENTAGE split: this is the percentage (0-100)
    // For SHARES split: this is the number of shares
    private BigDecimal value;
    
    // For UNEQUAL split: this is the amount owed
    private BigDecimal amountOwed;
}

