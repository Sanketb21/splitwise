package com.splitwise.expenseservice.service.impl;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.common.exception.BadRequestException;
import com.splitwise.common.exception.ResourceNotFoundException;
import com.splitwise.expenseservice.client.GroupServiceClient;
import com.splitwise.expenseservice.client.UserServiceClient;
import com.splitwise.expenseservice.dto.*;
import com.splitwise.expenseservice.entity.Expense;
import com.splitwise.expenseservice.entity.ExpenseParticipant;
import com.splitwise.expenseservice.repository.ExpenseParticipantRepository;
import com.splitwise.expenseservice.repository.ExpenseRepository;
import com.splitwise.expenseservice.service.ExpenseService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final UserServiceClient userServiceClient;
    private final GroupServiceClient groupServiceClient;
    
    private static final String SPLIT_TYPE_EQUAL = "EQUAL";
    private static final String SPLIT_TYPE_UNEQUAL = "UNEQUAL";
    private static final String SPLIT_TYPE_PERCENTAGE = "PERCENTAGE";
    private static final String SPLIT_TYPE_SHARES = "SHARES";
    private static final int DECIMAL_SCALE = 2;
    
    @Override
    public ExpenseResponse createExpense(ExpenseRequest expenseRequest, Long createdBy) {
        log.info("Creating expense for group {} by user {}", expenseRequest.getGroupId(), createdBy);
        
        // Validate user exists
        validateUserExists(createdBy);
        validateUserExists(expenseRequest.getPaidBy());
        
        // Validate group exists and get members
        List<GroupMemberClientResponse> groupMembers = validateGroupAndGetMembers(expenseRequest.getGroupId());
        
        // Validate all participants are group members
        Set<Long> memberUserIds = groupMembers.stream()
                .map(GroupMemberClientResponse::getUserId)
                .collect(Collectors.toSet());
        
        Set<Long> participantUserIds = expenseRequest.getParticipants().stream()
                .map(ExpenseParticipantRequest::getUserId)
                .collect(Collectors.toSet());
        
        if (!memberUserIds.containsAll(participantUserIds)) {
            throw new BadRequestException("All participants must be members of the group");
        }
        
        if (!memberUserIds.contains(expenseRequest.getPaidBy())) {
            throw new BadRequestException("The user who paid must be a member of the group");
        }
        
        // Create expense entity
        Expense expense = Expense.builder()
                .groupId(expenseRequest.getGroupId())
                .paidBy(expenseRequest.getPaidBy())
                .amount(expenseRequest.getAmount())
                .description(expenseRequest.getDescription())
                .expenseDate(expenseRequest.getExpenseDate() != null ? 
                        expenseRequest.getExpenseDate() : java.time.LocalDate.now())
                .splitType(expenseRequest.getSplitType().toUpperCase())
                .build();
        
        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created with ID: {}", savedExpense.getId());
        
        // Calculate and create participants based on split type
        List<ExpenseParticipant> participants = calculateAndCreateParticipants(
                savedExpense, expenseRequest);
        
        expenseParticipantRepository.saveAll(participants);
        log.info("Created {} participants for expense ID: {}", participants.size(), savedExpense.getId());
        
        return mapToExpenseResponse(savedExpense, participants);
    }
    
    /**
     * Calculate and create participants based on split type
     */
    private List<ExpenseParticipant> calculateAndCreateParticipants(
            Expense expense, ExpenseRequest expenseRequest) {
        
        String splitType = expenseRequest.getSplitType().toUpperCase();
        BigDecimal totalAmount = expense.getAmount();
        List<ExpenseParticipantRequest> participantRequests = expenseRequest.getParticipants();
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        
        switch (splitType) {
            case SPLIT_TYPE_EQUAL:
                participants = calculateEqualSplit(expense, participantRequests, totalAmount);
                break;
            case SPLIT_TYPE_UNEQUAL:
                participants = calculateUnequalSplit(expense, participantRequests, totalAmount);
                break;
            case SPLIT_TYPE_PERCENTAGE:
                participants = calculatePercentageSplit(expense, participantRequests, totalAmount);
                break;
            case SPLIT_TYPE_SHARES:
                participants = calculateSharesSplit(expense, participantRequests, totalAmount);
                break;
            default:
                throw new BadRequestException("Invalid split type: " + splitType);
        }
        
        return participants;
    }
    
    /**
     * Calculate equal split: divide amount equally among all participants
     */
    private List<ExpenseParticipant> calculateEqualSplit(
            Expense expense, List<ExpenseParticipantRequest> participantRequests, BigDecimal totalAmount) {
        
        int participantCount = participantRequests.size();
        if (participantCount == 0) {
            throw new BadRequestException("At least one participant is required");
        }
        
        // Divide amount equally
        BigDecimal amountPerPerson = totalAmount.divide(
                BigDecimal.valueOf(participantCount), DECIMAL_SCALE, RoundingMode.HALF_UP);
        
        // Handle rounding differences
        BigDecimal distributedAmount = amountPerPerson.multiply(BigDecimal.valueOf(participantCount));
        BigDecimal difference = totalAmount.subtract(distributedAmount);
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        for (int i = 0; i < participantRequests.size(); i++) {
            ExpenseParticipantRequest request = participantRequests.get(i);
            BigDecimal amountOwed = amountPerPerson;
            
            // Add rounding difference to the first participant
            if (i == 0 && difference.compareTo(BigDecimal.ZERO) != 0) {
                amountOwed = amountOwed.add(difference);
            }
            
            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expenseId(expense.getId())
                    .userId(request.getUserId())
                    .amountOwed(amountOwed)
                    .amountPaid(BigDecimal.ZERO)
                    .build();
            
            participants.add(participant);
        }
        
        return participants;
    }
    
    /**
     * Calculate unequal split: use custom amounts provided
     */
    private List<ExpenseParticipant> calculateUnequalSplit(
            Expense expense, List<ExpenseParticipantRequest> participantRequests, BigDecimal totalAmount) {
        
        // Validate that all participants have amountOwed specified
        BigDecimal sumOfAmounts = BigDecimal.ZERO;
        for (ExpenseParticipantRequest request : participantRequests) {
            if (request.getAmountOwed() == null) {
                throw new BadRequestException("Amount owed is required for UNEQUAL split");
            }
            if (request.getAmountOwed().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount owed must be greater than 0");
            }
            sumOfAmounts = sumOfAmounts.add(request.getAmountOwed());
        }
        
        // Validate sum equals total amount (with small tolerance for rounding)
        BigDecimal tolerance = new BigDecimal("0.01");
        BigDecimal difference = sumOfAmounts.subtract(totalAmount).abs();
        if (difference.compareTo(tolerance) > 0) {
            throw new BadRequestException(
                    String.format("Sum of amounts (%.2f) must equal total amount (%.2f)", 
                            sumOfAmounts, totalAmount));
        }
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        for (ExpenseParticipantRequest request : participantRequests) {
            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expenseId(expense.getId())
                    .userId(request.getUserId())
                    .amountOwed(request.getAmountOwed())
                    .amountPaid(BigDecimal.ZERO)
                    .build();
            
            participants.add(participant);
        }
        
        return participants;
    }
    
    /**
     * Calculate percentage split: use percentages provided (must sum to 100)
     */
    private List<ExpenseParticipant> calculatePercentageSplit(
            Expense expense, List<ExpenseParticipantRequest> participantRequests, BigDecimal totalAmount) {
        
        // Validate that all participants have percentage (value) specified
        BigDecimal sumOfPercentages = BigDecimal.ZERO;
        for (ExpenseParticipantRequest request : participantRequests) {
            if (request.getValue() == null) {
                throw new BadRequestException("Percentage is required for PERCENTAGE split");
            }
            if (request.getValue().compareTo(BigDecimal.ZERO) < 0 || 
                request.getValue().compareTo(new BigDecimal("100")) > 0) {
                throw new BadRequestException("Percentage must be between 0 and 100");
            }
            sumOfPercentages = sumOfPercentages.add(request.getValue());
        }
        
        // Validate sum equals 100 (with small tolerance for rounding)
        BigDecimal tolerance = new BigDecimal("0.01");
        BigDecimal difference = sumOfPercentages.subtract(new BigDecimal("100")).abs();
        if (difference.compareTo(tolerance) > 0) {
            throw new BadRequestException(
                    String.format("Sum of percentages (%.2f) must equal 100", sumOfPercentages));
        }
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        BigDecimal distributedAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < participantRequests.size(); i++) {
            ExpenseParticipantRequest request = participantRequests.get(i);
            BigDecimal percentage = request.getValue();
            
            // Calculate amount: (percentage / 100) * totalAmount
            BigDecimal amountOwed = totalAmount.multiply(percentage)
                    .divide(new BigDecimal("100"), DECIMAL_SCALE, RoundingMode.HALF_UP);
            
            distributedAmount = distributedAmount.add(amountOwed);
            
            // Handle rounding difference for the last participant
            if (i == participantRequests.size() - 1) {
                BigDecimal roundingDiff = totalAmount.subtract(distributedAmount);
                if (roundingDiff.compareTo(BigDecimal.ZERO) != 0) {
                    amountOwed = amountOwed.add(roundingDiff);
                }
            }
            
            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expenseId(expense.getId())
                    .userId(request.getUserId())
                    .amountOwed(amountOwed)
                    .amountPaid(BigDecimal.ZERO)
                    .build();
            
            participants.add(participant);
        }
        
        return participants;
    }
    
    /**
     * Calculate shares split: use shares provided (like 2:1:1 ratio)
     */
    private List<ExpenseParticipant> calculateSharesSplit(
            Expense expense, List<ExpenseParticipantRequest> participantRequests, BigDecimal totalAmount) {
        
        // Validate that all participants have shares (value) specified
        BigDecimal totalShares = BigDecimal.ZERO;
        for (ExpenseParticipantRequest request : participantRequests) {
            if (request.getValue() == null) {
                throw new BadRequestException("Shares are required for SHARES split");
            }
            if (request.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Shares must be greater than 0");
            }
            totalShares = totalShares.add(request.getValue());
        }
        
        if (totalShares.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Total shares must be greater than 0");
        }
        
        List<ExpenseParticipant> participants = new ArrayList<>();
        BigDecimal distributedAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < participantRequests.size(); i++) {
            ExpenseParticipantRequest request = participantRequests.get(i);
            BigDecimal shares = request.getValue();
            
            // Calculate amount: (shares / totalShares) * totalAmount
            BigDecimal amountOwed = totalAmount.multiply(shares)
                    .divide(totalShares, DECIMAL_SCALE, RoundingMode.HALF_UP);
            
            distributedAmount = distributedAmount.add(amountOwed);
            
            // Handle rounding difference for the last participant
            if (i == participantRequests.size() - 1) {
                BigDecimal roundingDiff = totalAmount.subtract(distributedAmount);
                if (roundingDiff.compareTo(BigDecimal.ZERO) != 0) {
                    amountOwed = amountOwed.add(roundingDiff);
                }
            }
            
            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expenseId(expense.getId())
                    .userId(request.getUserId())
                    .amountOwed(amountOwed)
                    .amountPaid(BigDecimal.ZERO)
                    .build();
            
            participants.add(participant);
        }
        
        return participants;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        log.info("Fetching expense by ID: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        
        List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(id);
        return mapToExpenseResponse(expense, participants);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByGroup(Long groupId, Pageable pageable) {
        log.info("Fetching expenses for group ID: {}", groupId);
        validateGroupExists(groupId);
        
        Page<Expense> expenses = expenseRepository.findByGroupId(groupId, pageable);
        return expenses.map(expense -> {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());
            return mapToExpenseResponse(expense, participants);
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByUser(Long userId, Pageable pageable) {
        log.info("Fetching expenses paid by user ID: {}", userId);
        validateUserExists(userId);
        
        Page<Expense> expenses = expenseRepository.findByPaidBy(userId, pageable);
        return expenses.map(expense -> {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());
            return mapToExpenseResponse(expense, participants);
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpensesByParticipant(Long userId, Pageable pageable) {
        log.info("Fetching expenses where user ID: {} is a participant", userId);
        validateUserExists(userId);
        
        Page<Expense> expenses = expenseRepository.findExpensesByParticipantUserId(userId, pageable);
        return expenses.map(expense -> {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());
            return mapToExpenseResponse(expense, participants);
        });
    }
    
    @Override
    public ExpenseResponse updateExpense(Long id, ExpenseRequest expenseRequest, Long updatedBy) {
        log.info("Updating expense ID: {} by user ID: {}", id, updatedBy);
        
        validateUserExists(updatedBy);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        
        // Only the person who paid can update (or we could add group admin check)
        if (!expense.getPaidBy().equals(updatedBy)) {
            throw new BadRequestException("Only the person who paid can update the expense");
        }
        
        // Validate group and participants
        validateGroupAndGetMembers(expenseRequest.getGroupId());
        validateUserExists(expenseRequest.getPaidBy());
        
        // Delete existing participants
        expenseParticipantRepository.deleteByExpenseId(id);
        
        // Update expense
        expense.setGroupId(expenseRequest.getGroupId());
        expense.setPaidBy(expenseRequest.getPaidBy());
        expense.setAmount(expenseRequest.getAmount());
        expense.setDescription(expenseRequest.getDescription());
        if (expenseRequest.getExpenseDate() != null) {
            expense.setExpenseDate(expenseRequest.getExpenseDate());
        }
        expense.setSplitType(expenseRequest.getSplitType().toUpperCase());
        
        Expense updatedExpense = expenseRepository.save(expense);
        
        // Recalculate and create participants
        List<ExpenseParticipant> participants = calculateAndCreateParticipants(updatedExpense, expenseRequest);
        expenseParticipantRepository.saveAll(participants);
        
        log.info("Expense updated successfully with ID: {}", updatedExpense.getId());
        return mapToExpenseResponse(updatedExpense, participants);
    }
    
    @Override
    public void deleteExpense(Long id, Long deletedBy) {
        log.info("Deleting expense ID: {} by user ID: {}", id, deletedBy);
        
        validateUserExists(deletedBy);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        
        // Only the person who paid can delete
        if (!expense.getPaidBy().equals(deletedBy)) {
            throw new BadRequestException("Only the person who paid can delete the expense");
        }
        
        // Delete participants (cascade should handle this, but explicit for clarity)
        expenseParticipantRepository.deleteByExpenseId(id);
        
        // Delete expense
        expenseRepository.delete(expense);
        log.info("Expense deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalanceByUserAndGroup(Long userId, Long groupId) {
        log.info("Fetching balance for user ID: {} in group ID: {}", userId, groupId);
        
        validateUserExists(userId);
        validateGroupExists(groupId);
        
        BigDecimal totalOwed = expenseParticipantRepository.getTotalAmountOwedByUserIdAndGroupId(userId, groupId);
        BigDecimal totalPaid = expenseParticipantRepository.getTotalAmountPaidByUserIdAndGroupId(userId, groupId);
        BigDecimal netBalance = totalOwed.subtract(totalPaid);
        BigDecimal outstandingBalance = netBalance.compareTo(BigDecimal.ZERO) > 0 ? netBalance : BigDecimal.ZERO;
        
        return BalanceResponse.builder()
                .userId(userId)
                .groupId(groupId)
                .totalOwed(totalOwed)
                .totalPaid(totalPaid)
                .netBalance(netBalance)
                .outstandingBalance(outstandingBalance)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BalanceResponse> getBalancesByGroup(Long groupId) {
        log.info("Fetching balances for group ID: {}", groupId);
        
        validateGroupExists(groupId);
        
        // Get all participants in the group
        List<ExpenseParticipant> participants = expenseParticipantRepository.findByGroupId(groupId);
        
        // Group by user ID
        Map<Long, List<ExpenseParticipant>> participantsByUser = participants.stream()
                .collect(Collectors.groupingBy(ExpenseParticipant::getUserId));
        
        List<BalanceResponse> balances = new ArrayList<>();
        for (Map.Entry<Long, List<ExpenseParticipant>> entry : participantsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<ExpenseParticipant> userParticipants = entry.getValue();
            
            BigDecimal totalOwed = userParticipants.stream()
                    .map(ExpenseParticipant::getAmountOwed)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalPaid = userParticipants.stream()
                    .map(ExpenseParticipant::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal netBalance = totalOwed.subtract(totalPaid);
            BigDecimal outstandingBalance = netBalance.compareTo(BigDecimal.ZERO) > 0 ? netBalance : BigDecimal.ZERO;
            
            balances.add(BalanceResponse.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .totalOwed(totalOwed)
                    .totalPaid(totalPaid)
                    .netBalance(netBalance)
                    .outstandingBalance(outstandingBalance)
                    .build());
        }
        
        return balances;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalanceByUser(Long userId) {
        log.info("Fetching balance for user ID: {}", userId);
        
        validateUserExists(userId);
        
        BigDecimal totalOwed = expenseParticipantRepository.getTotalAmountOwedByUserId(userId);
        BigDecimal totalPaid = expenseParticipantRepository.getTotalAmountPaidByUserId(userId);
        BigDecimal netBalance = totalOwed.subtract(totalPaid);
        BigDecimal outstandingBalance = netBalance.compareTo(BigDecimal.ZERO) > 0 ? netBalance : BigDecimal.ZERO;
        
        return BalanceResponse.builder()
                .userId(userId)
                .groupId(null)
                .totalOwed(totalOwed)
                .totalPaid(totalPaid)
                .netBalance(netBalance)
                .outstandingBalance(outstandingBalance)
                .build();
    }
    
    /**
     * Validates that the given user exists by calling the User Service
     */
    private void validateUserExists(Long userId) {
        try {
            ApiResponse<UserClientResponse> response = userServiceClient.getUserById(userId);
            if (response == null) {
                log.warn("User Service is unavailable (circuit breaker fallback). Cannot validate user ID: {}", userId);
                throw new BadRequestException("User Service is temporarily unavailable. Please try again later.");
            }
            if (!response.isSuccess() || response.getData() == null || 
                !Boolean.TRUE.equals(response.getData().getIsActive())) {
                throw new ResourceNotFoundException("User", "id", userId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("User", "id", userId);
        } catch (FeignException ex) {
            log.error("Failed to validate user {}: Status={}, Message={}", userId, ex.status(), ex.getMessage());
            throw new BadRequestException("Unable to validate user at the moment. Please try again later.");
        }
    }
    
    /**
     * Validates that the given group exists and returns its members
     */
    private List<GroupMemberClientResponse> validateGroupAndGetMembers(Long groupId) {
        try {
            ApiResponse<GroupClientResponse> groupResponse = groupServiceClient.getGroupById(groupId);
            if (groupResponse == null || !groupResponse.isSuccess() || groupResponse.getData() == null) {
                throw new ResourceNotFoundException("Group", "id", groupId);
            }
            
            ApiResponse<List<GroupMemberClientResponse>> membersResponse = groupServiceClient.getGroupMembers(groupId);
            if (membersResponse == null || !membersResponse.isSuccess() || membersResponse.getData() == null) {
                throw new BadRequestException("Unable to fetch group members. Please try again later.");
            }
            
            return membersResponse.getData();
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Group", "id", groupId);
        } catch (FeignException ex) {
            log.error("Failed to validate group {}: Status={}, Message={}", groupId, ex.status(), ex.getMessage());
            throw new BadRequestException("Unable to validate group at the moment. Please try again later.");
        }
    }
    
    /**
     * Validates that the given group exists
     */
    private void validateGroupExists(Long groupId) {
        try {
            ApiResponse<GroupClientResponse> response = groupServiceClient.getGroupById(groupId);
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new ResourceNotFoundException("Group", "id", groupId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Group", "id", groupId);
        } catch (FeignException ex) {
            log.error("Failed to validate group {}: Status={}, Message={}", groupId, ex.status(), ex.getMessage());
            throw new BadRequestException("Unable to validate group at the moment. Please try again later.");
        }
    }
    
    /**
     * Maps Expense entity and participants to ExpenseResponse DTO
     */
    private ExpenseResponse mapToExpenseResponse(Expense expense, List<ExpenseParticipant> participants) {
        List<ExpenseParticipantResponse> participantResponses = participants.stream()
                .map(this::mapToParticipantResponse)
                .collect(Collectors.toList());
        
        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroupId())
                .paidBy(expense.getPaidBy())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .splitType(expense.getSplitType())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .participants(participantResponses)
                .build();
    }
    
    /**
     * Maps ExpenseParticipant entity to ExpenseParticipantResponse DTO
     */
    private ExpenseParticipantResponse mapToParticipantResponse(ExpenseParticipant participant) {
        return ExpenseParticipantResponse.builder()
                .id(participant.getId())
                .expenseId(participant.getExpenseId())
                .userId(participant.getUserId())
                .amountOwed(participant.getAmountOwed())
                .amountPaid(participant.getAmountPaid())
                .createdAt(participant.getCreatedAt())
                .build();
    }
}

