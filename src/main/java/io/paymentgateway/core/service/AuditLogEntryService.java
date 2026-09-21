package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.AuditLogEntry}.
 */
public interface AuditLogEntryService {
    /**
     * Save a auditLogEntry.
     *
     * @param auditLogEntryDTO the entity to save.
     * @return the persisted entity.
     */
    AuditLogEntryDTO save(AuditLogEntryDTO auditLogEntryDTO);

    /**
     * Updates a auditLogEntry.
     *
     * @param auditLogEntryDTO the entity to update.
     * @return the persisted entity.
     */
    AuditLogEntryDTO update(AuditLogEntryDTO auditLogEntryDTO);

    /**
     * Partially updates a auditLogEntry.
     *
     * @param auditLogEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuditLogEntryDTO> partialUpdate(AuditLogEntryDTO auditLogEntryDTO);

    /**
     * Get the "id" auditLogEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuditLogEntryDTO> findOne(Long id);

    /**
     * Delete the "id" auditLogEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
