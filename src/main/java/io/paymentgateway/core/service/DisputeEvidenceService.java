package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.DisputeEvidence}.
 */
public interface DisputeEvidenceService {
    /**
     * Save a disputeEvidence.
     *
     * @param disputeEvidenceDTO the entity to save.
     * @return the persisted entity.
     */
    DisputeEvidenceDTO save(DisputeEvidenceDTO disputeEvidenceDTO);

    /**
     * Updates a disputeEvidence.
     *
     * @param disputeEvidenceDTO the entity to update.
     * @return the persisted entity.
     */
    DisputeEvidenceDTO update(DisputeEvidenceDTO disputeEvidenceDTO);

    /**
     * Partially updates a disputeEvidence.
     *
     * @param disputeEvidenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DisputeEvidenceDTO> partialUpdate(DisputeEvidenceDTO disputeEvidenceDTO);

    /**
     * Get the "id" disputeEvidence.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DisputeEvidenceDTO> findOne(Long id);

    /**
     * Delete the "id" disputeEvidence.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
