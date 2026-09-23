package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.JournalEntryDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.JournalEntry}.
 */
public interface JournalEntryService {
    /**
     * Save a journalEntry.
     *
     * @param journalEntryDTO the entity to save.
     * @return the persisted entity.
     */
    JournalEntryDTO save(JournalEntryDTO journalEntryDTO);

    /**
     * Updates a journalEntry.
     *
     * @param journalEntryDTO the entity to update.
     * @return the persisted entity.
     */
    JournalEntryDTO update(JournalEntryDTO journalEntryDTO);

    /**
     * Partially updates a journalEntry.
     *
     * @param journalEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<JournalEntryDTO> partialUpdate(JournalEntryDTO journalEntryDTO);

    /**
     * Get the "id" journalEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JournalEntryDTO> findOne(Long id);

    /**
     * Delete the "id" journalEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
