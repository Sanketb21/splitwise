package com.splitwise.expenseservice.repository;

import com.splitwise.expenseservice.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Expense entity
 * 
 * Provides CRUD operations and custom query methods for Expense management
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Find all expenses for a group
     * 
     * @param groupId the group ID
     * @return list of expenses for the group
     */
    List<Expense> findByGroupId(Long groupId);
    
    /**
     * Find all expenses for a group with pagination
     * 
     * @param groupId the group ID
     * @param pageable pagination information
     * @return page of expenses for the group
     */
    Page<Expense> findByGroupId(Long groupId, Pageable pageable);
    
    /**
     * Find all expenses paid by a user
     * 
     * @param paidBy the user ID who paid
     * @return list of expenses paid by the user
     */
    List<Expense> findByPaidBy(Long paidBy);
    
    /**
     * Find all expenses paid by a user with pagination
     * 
     * @param paidBy the user ID who paid
     * @param pageable pagination information
     * @return page of expenses paid by the user
     */
    Page<Expense> findByPaidBy(Long paidBy, Pageable pageable);
    
    /**
     * Find expenses for a group paid by a specific user
     * 
     * @param groupId the group ID
     * @param paidBy the user ID who paid
     * @return list of expenses
     */
    List<Expense> findByGroupIdAndPaidBy(Long groupId, Long paidBy);
    
    /**
     * Find expenses within a date range for a group
     * 
     * @param groupId the group ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of expenses in the date range
     */
    List<Expense> findByGroupIdAndExpenseDateBetween(Long groupId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find expenses within a date range for a group with pagination
     * 
     * @param groupId the group ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return page of expenses in the date range
     */
    Page<Expense> findByGroupIdAndExpenseDateBetween(Long groupId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find expenses by split type
     * 
     * @param splitType the split type (EQUAL, UNEQUAL, PERCENTAGE, SHARES)
     * @return list of expenses with the split type
     */
    List<Expense> findBySplitType(String splitType);
    
    /**
     * Find expenses for a group by split type
     * 
     * @param groupId the group ID
     * @param splitType the split type
     * @return list of expenses
     */
    List<Expense> findByGroupIdAndSplitType(Long groupId, String splitType);
    
    /**
     * Find expense by ID and group ID
     * 
     * @param id the expense ID
     * @param groupId the group ID
     * @return Optional containing the expense if found
     */
    Optional<Expense> findByIdAndGroupId(Long id, Long groupId);
    
    /**
     * Count expenses for a group
     * 
     * @param groupId the group ID
     * @return count of expenses in the group
     */
    long countByGroupId(Long groupId);
    
    /**
     * Count expenses paid by a user
     * 
     * @param paidBy the user ID
     * @return count of expenses paid by the user
     */
    long countByPaidBy(Long paidBy);
    
    /**
     * Get total amount of expenses for a group
     * 
     * @param groupId the group ID
     * @return total amount of all expenses in the group
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.groupId = :groupId")
    java.math.BigDecimal getTotalAmountByGroupId(@Param("groupId") Long groupId);
    
    /**
     * Get total amount of expenses paid by a user
     * 
     * @param paidBy the user ID
     * @return total amount of expenses paid by the user
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.paidBy = :paidBy")
    java.math.BigDecimal getTotalAmountByPaidBy(@Param("paidBy") Long paidBy);
    
    /**
     * Get total amount of expenses for a group within a date range
     * 
     * @param groupId the group ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return total amount of expenses in the date range
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.groupId = :groupId " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalAmountByGroupIdAndDateRange(
            @Param("groupId") Long groupId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find expenses where a user is a participant (via ExpenseParticipant)
     * 
     * @param userId the user ID
     * @return list of expenses where the user is a participant
     */
    @Query("SELECT DISTINCT e FROM Expense e JOIN ExpenseParticipant ep ON e.id = ep.expenseId " +
           "WHERE ep.userId = :userId")
    List<Expense> findExpensesByParticipantUserId(@Param("userId") Long userId);
    
    /**
     * Find expenses where a user is a participant with pagination
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of expenses where the user is a participant
     */
    @Query("SELECT DISTINCT e FROM Expense e JOIN ExpenseParticipant ep ON e.id = ep.expenseId " +
           "WHERE ep.userId = :userId")
    Page<Expense> findExpensesByParticipantUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find expenses for a group where a user is a participant
     * 
     * @param groupId the group ID
     * @param userId the user ID
     * @return list of expenses
     */
    @Query("SELECT DISTINCT e FROM Expense e JOIN ExpenseParticipant ep ON e.id = ep.expenseId " +
           "WHERE e.groupId = :groupId AND ep.userId = :userId")
    List<Expense> findExpensesByGroupIdAndParticipantUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    /**
     * Search expenses by description (case-insensitive)
     * 
     * @param groupId the group ID
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.groupId = :groupId AND " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Expense> searchExpensesByDescription(@Param("groupId") Long groupId, 
                                               @Param("searchTerm") String searchTerm, 
                                               Pageable pageable);
}

