package com.splitwise.expenseservice.repository;

import com.splitwise.expenseservice.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExpenseParticipant entity
 * 
 * Provides CRUD operations and custom query methods for ExpenseParticipant management
 */
@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
    
    /**
     * Find all participants for an expense
     * 
     * @param expenseId the expense ID
     * @return list of participants
     */
    List<ExpenseParticipant> findByExpenseId(Long expenseId);
    
    /**
     * Find all participants for multiple expenses
     * 
     * @param expenseIds list of expense IDs
     * @return list of participants
     */
    List<ExpenseParticipant> findByExpenseIdIn(List<Long> expenseIds);
    
    /**
     * Find all expenses a user is participating in
     * 
     * @param userId the user ID
     * @return list of expense participants
     */
    List<ExpenseParticipant> findByUserId(Long userId);
    
    /**
     * Find a specific participant
     * 
     * @param expenseId the expense ID
     * @param userId the user ID
     * @return Optional containing the participant if found
     */
    Optional<ExpenseParticipant> findByExpenseIdAndUserId(Long expenseId, Long userId);
    
    /**
     * Check if a user is a participant in an expense
     * 
     * @param expenseId the expense ID
     * @param userId the user ID
     * @return true if user is a participant, false otherwise
     */
    boolean existsByExpenseIdAndUserId(Long expenseId, Long userId);
    
    /**
     * Count participants in an expense
     * 
     * @param expenseId the expense ID
     * @return count of participants
     */
    long countByExpenseId(Long expenseId);
    
    /**
     * Count expenses a user is participating in
     * 
     * @param userId the user ID
     * @return count of expenses the user is participating in
     */
    long countByUserId(Long userId);
    
    /**
     * Get total amount owed by a user for all expenses
     * 
     * @param userId the user ID
     * @return total amount owed
     */
    @Query("SELECT COALESCE(SUM(ep.amountOwed), 0) FROM ExpenseParticipant ep WHERE ep.userId = :userId")
    BigDecimal getTotalAmountOwedByUserId(@Param("userId") Long userId);
    
    /**
     * Get total amount paid by a user for all expenses
     * 
     * @param userId the user ID
     * @return total amount paid
     */
    @Query("SELECT COALESCE(SUM(ep.amountPaid), 0) FROM ExpenseParticipant ep WHERE ep.userId = :userId")
    BigDecimal getTotalAmountPaidByUserId(@Param("userId") Long userId);
    
    /**
     * Get total amount owed by a user for expenses in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return total amount owed
     */
    @Query("SELECT COALESCE(SUM(ep.amountOwed), 0) FROM ExpenseParticipant ep " +
           "JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId")
    BigDecimal getTotalAmountOwedByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    /**
     * Get total amount paid by a user for expenses in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return total amount paid
     */
    @Query("SELECT COALESCE(SUM(ep.amountPaid), 0) FROM ExpenseParticipant ep " +
           "JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId")
    BigDecimal getTotalAmountPaidByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    /**
     * Get net balance for a user (amount owed - amount paid)
     * 
     * @param userId the user ID
     * @return net balance (positive means owes money, negative means is owed money)
     */
    @Query("SELECT COALESCE(SUM(ep.amountOwed), 0) - COALESCE(SUM(ep.amountPaid), 0) " +
           "FROM ExpenseParticipant ep WHERE ep.userId = :userId")
    BigDecimal getNetBalanceByUserId(@Param("userId") Long userId);
    
    /**
     * Get net balance for a user in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return net balance in the group
     */
    @Query("SELECT COALESCE(SUM(ep.amountOwed), 0) - COALESCE(SUM(ep.amountPaid), 0) " +
           "FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId")
    BigDecimal getNetBalanceByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    /**
     * Get all participants for expenses in a group
     * 
     * @param groupId the group ID
     * @return list of participants
     */
    @Query("SELECT ep FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE e.groupId = :groupId")
    List<ExpenseParticipant> findByGroupId(@Param("groupId") Long groupId);
    
    /**
     * Get participants for a specific user in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return list of participants
     */
    @Query("SELECT ep FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId")
    List<ExpenseParticipant> findByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    /**
     * Get all users who owe money in a group (amount owed > amount paid)
     * 
     * @param groupId the group ID
     * @return list of participants with outstanding balance
     */
    @Query("SELECT ep FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE e.groupId = :groupId AND ep.amountOwed > ep.amountPaid")
    List<ExpenseParticipant> findOutstandingBalancesByGroupId(@Param("groupId") Long groupId);
    
    /**
     * Get outstanding balance for a user in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return list of participants with outstanding balance
     */
    @Query("SELECT ep FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId AND ep.amountOwed > ep.amountPaid")
    List<ExpenseParticipant> findOutstandingBalancesByUserIdAndGroupId(@Param("userId") Long userId, 
                                                                        @Param("groupId") Long groupId);
    
    /**
     * Get total outstanding amount for a user in a group
     * 
     * @param userId the user ID
     * @param groupId the group ID
     * @return total outstanding amount (amount owed - amount paid)
     */
    @Query("SELECT COALESCE(SUM(ep.amountOwed - ep.amountPaid), 0) " +
           "FROM ExpenseParticipant ep JOIN Expense e ON ep.expenseId = e.id " +
           "WHERE ep.userId = :userId AND e.groupId = :groupId AND ep.amountOwed > ep.amountPaid")
    BigDecimal getTotalOutstandingByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
    
    /**
     * Delete all participants for an expense
     * 
     * @param expenseId the expense ID
     */
    void deleteByExpenseId(Long expenseId);
}

