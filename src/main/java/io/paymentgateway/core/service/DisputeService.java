package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.DisputeDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.Dispute}.
 */
public interface DisputeService {
    /**
     * Save a dispute.
     *
     * @param disputeDTO the entity to save.
     * @return the persisted entity.
     */
    DisputeDTO save(DisputeDTO disputeDTO);

    /**
     * Updates a dispute.
     *
     * @param disputeDTO the entity to update.
     * @return the persisted entity.
     */
    DisputeDTO update(DisputeDTO disputeDTO);

    /**
     * Partially updates a dispute.
     *
     * @param disputeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DisputeDTO> partialUpdate(DisputeDTO disputeDTO);

    /**
     * Get the "id" dispute.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DisputeDTO> findOne(Long id);

    /**
     * Delete the "id" dispute.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
