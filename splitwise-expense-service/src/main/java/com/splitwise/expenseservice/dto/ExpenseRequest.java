package com.splitwise.expenseservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for expense creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    @NotNull(message = "Paid by user ID is required")
    private Long paidBy;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal amount;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private LocalDate expenseDate;
    
    @NotBlank(message = "Split type is required")
    @Pattern(regexp = "EQUAL|UNEQUAL|PERCENTAGE|SHARES", 
             message = "Split type must be EQUAL, UNEQUAL, PERCENTAGE, or SHARES")
    private String splitType;
    
    @NotEmpty(message = "Participants list cannot be empty")
    @Size(min = 1, message = "At least one participant is required")
    private List<ExpenseParticipantRequest> participants;
}

