package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.TenantWalletDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.TenantWallet}.
 */
public interface TenantWalletService {
    /**
     * Save a tenantWallet.
     *
     * @param tenantWalletDTO the entity to save.
     * @return the persisted entity.
     */
    TenantWalletDTO save(TenantWalletDTO tenantWalletDTO);

    /**
     * Updates a tenantWallet.
     *
     * @param tenantWalletDTO the entity to update.
     * @return the persisted entity.
     */
    TenantWalletDTO update(TenantWalletDTO tenantWalletDTO);

    /**
     * Partially updates a tenantWallet.
     *
     * @param tenantWalletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TenantWalletDTO> partialUpdate(TenantWalletDTO tenantWalletDTO);

    /**
     * Get the "id" tenantWallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TenantWalletDTO> findOne(Long id);

    /**
     * Delete the "id" tenantWallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
