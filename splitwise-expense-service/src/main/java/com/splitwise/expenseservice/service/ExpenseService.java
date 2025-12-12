package com.splitwise.expenseservice.service;

import com.splitwise.expenseservice.dto.BalanceResponse;
import com.splitwise.expenseservice.dto.ExpenseRequest;
import com.splitwise.expenseservice.dto.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Expense operations
 */
public interface ExpenseService {
    
    /**
     * Create a new expense with split calculation
     * 
     * @param expenseRequest the expense creation request
     * @param createdBy the user ID who is creating the expense
     * @return the created expense response
     */
    ExpenseResponse createExpense(ExpenseRequest expenseRequest, Long createdBy);
    
    /**
     * Get expense by ID
     * 
     * @param id the expense ID
     * @return the expense response
     */
    ExpenseResponse getExpenseById(Long id);
    
    /**
     * Get all expenses for a group
     * 
     * @param groupId the group ID
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByGroup(Long groupId, Pageable pageable);
    
    /**
     * Get all expenses paid by a user
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByUser(Long userId, Pageable pageable);
    
    /**
     * Get all expenses where a user is a participant
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of expenses
     */
    Page<ExpenseResponse> getExpensesByParticipant(Long userId, Pageable pageable);
    
    /**
     * Update an expense
     * 
     * @param id the expense ID
     * @param expenseRequest the update request
     * @param updatedBy the user ID making the request
     * @return the updated expense response
     */
    ExpenseResponse updateExpense(Long id, ExpenseRequest expenseRequest, Long updatedBy);
    
    /**
     * Delete an expense
     * 
     * @param id the expense ID
     * @param deletedBy the user ID making the request
     */
    void deleteExpense(Long id, Long deletedBy);
    
    /**
     * Get balance for a user in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return the balance response
     */
    BalanceResponse getBalanceByUserAndGroup(Long userId, Long groupId);
    
    /**
     * Get all balances for a group
     * 
     * @param groupId the group ID
     * @return list of balance responses
     */
    List<BalanceResponse> getBalancesByGroup(Long groupId);
    
    /**
     * Get balance for a user across all groups
     * 
     * @param userId the user ID
     * @return the balance response
     */
    BalanceResponse getBalanceByUser(Long userId);
}

