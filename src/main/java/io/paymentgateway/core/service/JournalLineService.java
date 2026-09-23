package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.JournalLineDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.JournalLine}.
 */
public interface JournalLineService {
    /**
     * Save a journalLine.
     *
     * @param journalLineDTO the entity to save.
     * @return the persisted entity.
     */
    JournalLineDTO save(JournalLineDTO journalLineDTO);

    /**
     * Updates a journalLine.
     *
     * @param journalLineDTO the entity to update.
     * @return the persisted entity.
     */
    JournalLineDTO update(JournalLineDTO journalLineDTO);

    /**
     * Partially updates a journalLine.
     *
     * @param journalLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<JournalLineDTO> partialUpdate(JournalLineDTO journalLineDTO);

    /**
     * Get the "id" journalLine.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JournalLineDTO> findOne(Long id);

    /**
     * Delete the "id" journalLine.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
