package com.splitwise.expenseservice.controller;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.expenseservice.dto.BalanceResponse;
import com.splitwise.expenseservice.dto.ExpenseRequest;
import com.splitwise.expenseservice.dto.ExpenseResponse;
import com.splitwise.expenseservice.dto.SimplifiedDebtResponse;
import com.splitwise.expenseservice.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Expense operations
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Create a new expense
     * 
     * @param expenseRequest the expense creation request
     * @param createdBy      the user ID who is creating the expense
     * @return the created expense response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest expenseRequest,
            @RequestParam("createdBy") Long createdBy) {
        log.info("Create expense request by user {} for group {}", createdBy, expenseRequest.getGroupId());
        ExpenseResponse response = expenseService.createExpense(expenseRequest, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense created successfully", response));
    }

    /**
     * Get expense by ID
     * 
     * @param id the expense ID
     * @return the expense response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable("id") Long id) {
        log.info("Get expense request for ID: {}", id);
        ExpenseResponse response = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all expenses for a group with pagination
     * 
     * @param groupId the group ID
     * @param page    page number (default: 0)
     * @param size    page size (default: 10)
     * @param sortBy  field to sort by (default: "id")
     * @param sortDir sort direction: "asc" or "desc" (default: "desc")
     * @return page of expenses
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByGroup(
            @PathVariable("groupId") Long groupId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "expenseDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        log.info("Get expenses for group ID: {}", groupId);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExpenseResponse> result = expenseService.getExpensesByGroup(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get all expenses paid by a user with pagination
     * 
     * @param userId  the user ID
     * @param page    page number (default: 0)
     * @param size    page size (default: 10)
     * @param sortBy  field to sort by (default: "expenseDate")
     * @param sortDir sort direction: "asc" or "desc" (default: "desc")
     * @return page of expenses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "expenseDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        log.info("Get expenses paid by user ID: {}", userId);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExpenseResponse> result = expenseService.getExpensesByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get all expenses where a user is a participant with pagination
     * 
     * @param userId  the user ID
     * @param page    page number (default: 0)
     * @param size    page size (default: 10)
     * @param sortBy  field to sort by (default: "expenseDate")
     * @param sortDir sort direction: "asc" or "desc" (default: "desc")
     * @return page of expenses
     */
    @GetMapping("/participant/{userId}")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpensesByParticipant(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "expenseDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        log.info("Get expenses where user ID: {} is a participant", userId);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ExpenseResponse> result = expenseService.getExpensesByParticipant(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Update an expense
     * 
     * @param id             the expense ID
     * @param expenseRequest the update request
     * @param updatedBy      the user ID making the request
     * @return the updated expense response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExpenseRequest expenseRequest,
            @RequestParam("updatedBy") Long updatedBy) {
        log.info("Update expense request for ID: {} by user {}", id, updatedBy);
        ExpenseResponse response = expenseService.updateExpense(id, expenseRequest, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    /**
     * Delete an expense
     * 
     * @param id        the expense ID
     * @param deletedBy the user ID making the request
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable("id") Long id,
            @RequestParam("deletedBy") Long deletedBy) {
        log.info("Delete expense request for ID: {} by user {}", id, deletedBy);
        expenseService.deleteExpense(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    /**
     * Get balance for a user in a specific group
     * 
     * @param userId  the user ID
     * @param groupId the group ID
     * @return the balance response
     */
    @GetMapping("/balances/user/{userId}/group/{groupId}")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalanceByUserAndGroup(
            @PathVariable("userId") Long userId,
            @PathVariable("groupId") Long groupId) {
        log.info("Get balance for user ID: {} in group ID: {}", userId, groupId);
        BalanceResponse response = expenseService.getBalanceByUserAndGroup(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all balances for a group
     * 
     * @param groupId the group ID
     * @return list of balance responses
     */
    @GetMapping("/balances/group/{groupId}")
    public ResponseEntity<ApiResponse<List<BalanceResponse>>> getBalancesByGroup(
            @PathVariable("groupId") Long groupId) {
        log.info("Get balances for group ID: {}", groupId);
        List<BalanceResponse> response = expenseService.getBalancesByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get balance for a user across all groups
     * 
     * @param userId the user ID
     * @return the balance response
     */
    @GetMapping("/balances/user/{userId}")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalanceByUser(
            @PathVariable("userId") Long userId) {
        log.info("Get balance for user ID: {}", userId);
        BalanceResponse response = expenseService.getBalanceByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get simplified debts for a group (minimize transactions)
     * 
     * @param groupId the group ID
     * @return list of simplified debt transactions
     */
    @GetMapping("/simplified-debts/group/{groupId}")
    public ResponseEntity<ApiResponse<List<SimplifiedDebtResponse>>> getSimplifiedDebtsByGroup(
            @PathVariable("groupId") Long groupId) {
        log.info("Get simplified debts for group ID: {}", groupId);
        List<SimplifiedDebtResponse> response = expenseService.getSimplifiedDebtsByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
