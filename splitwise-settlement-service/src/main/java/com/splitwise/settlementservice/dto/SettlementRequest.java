package com.splitwise.settlementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {

    private Long payerId;

    private Long payeeId;

    private Long groupId;

    private BigDecimal amount;

    private String notes;
}
