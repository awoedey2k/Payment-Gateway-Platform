package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.AmlCheckDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.AmlCheck}.
 */
public interface AmlCheckService {
    /**
     * Save a amlCheck.
     *
     * @param amlCheckDTO the entity to save.
     * @return the persisted entity.
     */
    AmlCheckDTO save(AmlCheckDTO amlCheckDTO);

    /**
     * Updates a amlCheck.
     *
     * @param amlCheckDTO the entity to update.
     * @return the persisted entity.
     */
    AmlCheckDTO update(AmlCheckDTO amlCheckDTO);

    /**
     * Partially updates a amlCheck.
     *
     * @param amlCheckDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AmlCheckDTO> partialUpdate(AmlCheckDTO amlCheckDTO);

    /**
     * Get the "id" amlCheck.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AmlCheckDTO> findOne(Long id);

    /**
     * Delete the "id" amlCheck.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
