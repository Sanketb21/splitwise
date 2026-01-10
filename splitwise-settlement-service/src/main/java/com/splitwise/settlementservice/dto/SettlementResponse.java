package com.splitwise.settlementservice.dto;

import com.splitwise.settlementservice.enums.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {

    private Long id;

    private Long payerId;

    private Long payeeId;

    private Long groupId;

    private BigDecimal amount;

    private LocalDateTime settlementDate;

    private SettlementStatus status;

    private String notes;

    private LocalDateTime createdAt;
}
