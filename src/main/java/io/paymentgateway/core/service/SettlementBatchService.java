package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.SettlementBatchDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.SettlementBatch}.
 */
public interface SettlementBatchService {
    /**
     * Save a settlementBatch.
     *
     * @param settlementBatchDTO the entity to save.
     * @return the persisted entity.
     */
    SettlementBatchDTO save(SettlementBatchDTO settlementBatchDTO);

    /**
     * Updates a settlementBatch.
     *
     * @param settlementBatchDTO the entity to update.
     * @return the persisted entity.
     */
    SettlementBatchDTO update(SettlementBatchDTO settlementBatchDTO);

    /**
     * Partially updates a settlementBatch.
     *
     * @param settlementBatchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SettlementBatchDTO> partialUpdate(SettlementBatchDTO settlementBatchDTO);

    /**
     * Get the "id" settlementBatch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SettlementBatchDTO> findOne(Long id);

    /**
     * Delete the "id" settlementBatch.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
