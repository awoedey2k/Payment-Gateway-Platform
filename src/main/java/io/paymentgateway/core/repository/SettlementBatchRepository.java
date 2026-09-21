package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.SettlementBatch;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SettlementBatch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SettlementBatchRepository extends JpaRepository<SettlementBatch, Long>, JpaSpecificationExecutor<SettlementBatch> {}
