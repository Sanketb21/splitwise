package com.splitwise.settlementservice.service;

import com.splitwise.settlementservice.dto.SettlementRequest;
import com.splitwise.settlementservice.dto.SettlementResponse;
import com.splitwise.settlementservice.enums.SettlementStatus;

import java.util.List;

public interface SettlementService {

    /**
     * Initiate a new settlement
     */
    SettlementResponse initiateSettlement(SettlementRequest request);

    /**
     * Get details of a specific settlement
     */
    SettlementResponse getSettlement(Long id);

    /**
     * Update the status of a settlement
     */
    SettlementResponse updateStatus(Long id, SettlementStatus status);

    /**
     * Get settlement history for a user (both as payer and payee)
     */
    List<SettlementResponse> getUserSettlements(Long userId);

    /**
     * Get settlements for a specific group
     */
    List<SettlementResponse> getGroupSettlements(Long groupId);

    /**
     * Delete a settlement by ID
     */
    void deleteSettlement(Long id);
}
