package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.LedgerAccount}.
 */
public interface LedgerAccountService {
    /**
     * Save a ledgerAccount.
     *
     * @param ledgerAccountDTO the entity to save.
     * @return the persisted entity.
     */
    LedgerAccountDTO save(LedgerAccountDTO ledgerAccountDTO);

    /**
     * Updates a ledgerAccount.
     *
     * @param ledgerAccountDTO the entity to update.
     * @return the persisted entity.
     */
    LedgerAccountDTO update(LedgerAccountDTO ledgerAccountDTO);

    /**
     * Partially updates a ledgerAccount.
     *
     * @param ledgerAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LedgerAccountDTO> partialUpdate(LedgerAccountDTO ledgerAccountDTO);

    /**
     * Get the "id" ledgerAccount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LedgerAccountDTO> findOne(Long id);

    /**
     * Delete the "id" ledgerAccount.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
