package com.splitwise.settlementservice.entity;

import com.splitwise.settlementservice.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a settlement (payment) between two users
 */
@Entity
@Table(name = "settlement", indexes = {
        @Index(name = "idx_payer", columnList = "payer_id"),
        @Index(name = "idx_payee", columnList = "payee_id"),
        @Index(name = "idx_group", columnList = "group_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID who is making the payment
     */
    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    /**
     * User ID who is receiving the payment
     */
    @Column(name = "payee_id", nullable = false)
    private Long payeeId;

    /**
     * Optional group ID for group-specific settlements
     */
    @Column(name = "group_id")
    private Long groupId;

    /**
     * Amount being settled
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Date and time when the settlement occurred
     */
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;

    /**
     * Status of the settlement
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private SettlementStatus status;

    /**
     * Optional notes about the settlement
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Timestamp when the record was created
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
