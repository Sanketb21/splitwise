package com.splitwise.settlementservice.service;

import com.splitwise.settlementservice.dto.SettlementRequest;
import com.splitwise.settlementservice.dto.SettlementResponse;
import com.splitwise.settlementservice.entity.Settlement;
import com.splitwise.settlementservice.enums.SettlementStatus;
import com.splitwise.settlementservice.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;

    @Override
    @Transactional
    public SettlementResponse initiateSettlement(SettlementRequest request) {
        log.info("Initiating settlement from user {} to user {} for amount {}",
                request.getPayerId(), request.getPayeeId(), request.getAmount());

        // Basic validation
        if (request.getPayerId().equals(request.getPayeeId())) {
            throw new IllegalArgumentException("Payer and payee cannot be the same user");
        }

        Settlement settlement = Settlement.builder()
                .payerId(request.getPayerId())
                .payeeId(request.getPayeeId())
                .groupId(request.getGroupId())
                .amount(request.getAmount())
                // .settlementDate(LocalDateTime.now()) // Typically set when COMPLETED
                .status(SettlementStatus.PENDING)
                .notes(request.getNotes())
                .build();

        Settlement savedSettlement = settlementRepository.save(settlement);
        log.info("Settlement initiated with ID: {}", savedSettlement.getId());

        return mapToResponse(savedSettlement);
    }

    @Override
    public SettlementResponse getSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Settlement not found with id: " + id));
        return mapToResponse(settlement);
    }

    @Override
    @Transactional
    public SettlementResponse updateStatus(Long id, SettlementStatus status) {
        log.info("Updating settlement {} status to {}", id, status);

        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Settlement not found with id: " + id));

        settlement.setStatus(status);

        // If completed, set the settlement date
        if (status == SettlementStatus.COMPLETED && settlement.getSettlementDate() == null) {
            settlement.setSettlementDate(LocalDateTime.now());
        }

        Settlement updatedSettlement = settlementRepository.save(settlement);
        return mapToResponse(updatedSettlement);
    }

    @Override
    public List<SettlementResponse> getUserSettlements(Long userId) {
        return settlementRepository.findByPayerIdOrPayeeId(userId, userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SettlementResponse> getGroupSettlements(Long groupId) {
        return settlementRepository.findByGroupId(groupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSettlement(Long id) {
        log.info("Deleting settlement with ID: {}", id);
        Settlement settlement = settlementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Settlement not found with id: " + id));
        settlementRepository.delete(settlement);
        log.info("Settlement {} deleted successfully", id);
    }

    private SettlementResponse mapToResponse(Settlement settlement) {
        return SettlementResponse.builder()
                .id(settlement.getId())
                .payerId(settlement.getPayerId())
                .payeeId(settlement.getPayeeId())
                .groupId(settlement.getGroupId())
                .amount(settlement.getAmount())
                .settlementDate(settlement.getSettlementDate())
                .status(settlement.getStatus())
                .notes(settlement.getNotes())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
