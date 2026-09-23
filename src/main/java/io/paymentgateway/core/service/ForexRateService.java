package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.ForexRateDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.ForexRate}.
 */
public interface ForexRateService {
    /**
     * Save a forexRate.
     *
     * @param forexRateDTO the entity to save.
     * @return the persisted entity.
     */
    ForexRateDTO save(ForexRateDTO forexRateDTO);

    /**
     * Updates a forexRate.
     *
     * @param forexRateDTO the entity to update.
     * @return the persisted entity.
     */
    ForexRateDTO update(ForexRateDTO forexRateDTO);

    /**
     * Partially updates a forexRate.
     *
     * @param forexRateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ForexRateDTO> partialUpdate(ForexRateDTO forexRateDTO);

    /**
     * Get the "id" forexRate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ForexRateDTO> findOne(Long id);

    /**
     * Delete the "id" forexRate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
