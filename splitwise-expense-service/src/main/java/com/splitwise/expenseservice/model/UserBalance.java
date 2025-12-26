package com.splitwise.expenseservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Internal model class to represent a user's balance during debt simplification
 * This is used internally by the debt simplification algorithm and is not
 * exposed via API
 */
@Data
@AllArgsConstructor
public class UserBalance {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Balance amount (positive for creditors, negative for debtors)
     */
    private BigDecimal amount;
}
