package com.splitwise.settlementservice.enums;

/**
 * Enum representing the status of a settlement
 */
public enum SettlementStatus {
    /**
     * Settlement has been created but not yet completed
     */
    PENDING,

    /**
     * Settlement has been successfully completed
     */
    COMPLETED,

    /**
     * Settlement has been cancelled
     */
    CANCELLED
}
