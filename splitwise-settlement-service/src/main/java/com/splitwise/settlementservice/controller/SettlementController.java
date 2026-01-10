package com.splitwise.settlementservice.controller;

import com.splitwise.settlementservice.dto.SettlementRequest;
import com.splitwise.settlementservice.dto.SettlementResponse;
import com.splitwise.settlementservice.enums.SettlementStatus;
import com.splitwise.settlementservice.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * Initiate a new settlement
     */
    @PostMapping
    public ResponseEntity<SettlementResponse> initiateSettlement(@RequestBody SettlementRequest request) {
        SettlementResponse response = settlementService.initiateSettlement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get settlement details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SettlementResponse> getSettlement(@PathVariable Long id) {
        return ResponseEntity.ok(settlementService.getSettlement(id));
    }

    /**
     * Update settlement status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<SettlementResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam SettlementStatus status) {
        return ResponseEntity.ok(settlementService.updateStatus(id, status));
    }

    /**
     * Get settlement history for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SettlementResponse>> getUserSettlements(@PathVariable Long userId) {
        return ResponseEntity.ok(settlementService.getUserSettlements(userId));
    }

    /**
     * Get settlements for a group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SettlementResponse>> getGroupSettlements(@PathVariable Long groupId) {
        return ResponseEntity.ok(settlementService.getGroupSettlements(groupId));
    }

    /**
     * Delete a settlement by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSettlement(@PathVariable Long id) {
        settlementService.deleteSettlement(id);
        return ResponseEntity.noContent().build();
    }
}
