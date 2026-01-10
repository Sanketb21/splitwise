package com.splitwise.settlementservice.repository;

import com.splitwise.settlementservice.entity.Settlement;
import com.splitwise.settlementservice.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /**
     * Find all settlements where the user is either the payer or the payee
     */
    List<Settlement> findByPayerIdOrPayeeId(Long payerId, Long payeeId);

    /**
     * Find all settlements by group ID
     */
    List<Settlement> findByGroupId(Long groupId);

    /**
     * Find all settlements where user is payer with specific status
     */
    List<Settlement> findByPayerIdAndStatus(Long payerId, SettlementStatus status);

    /**
     * Find all settlements where user is payee with specific status
     */
    List<Settlement> findByPayeeIdAndStatus(Long payeeId, SettlementStatus status);
}
