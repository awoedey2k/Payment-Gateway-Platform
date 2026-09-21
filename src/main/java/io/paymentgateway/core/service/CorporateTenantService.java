package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.CorporateTenant}.
 */
public interface CorporateTenantService {
    /**
     * Save a corporateTenant.
     *
     * @param corporateTenantDTO the entity to save.
     * @return the persisted entity.
     */
    CorporateTenantDTO save(CorporateTenantDTO corporateTenantDTO);

    /**
     * Updates a corporateTenant.
     *
     * @param corporateTenantDTO the entity to update.
     * @return the persisted entity.
     */
    CorporateTenantDTO update(CorporateTenantDTO corporateTenantDTO);

    /**
     * Partially updates a corporateTenant.
     *
     * @param corporateTenantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CorporateTenantDTO> partialUpdate(CorporateTenantDTO corporateTenantDTO);

    /**
     * Get the "id" corporateTenant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CorporateTenantDTO> findOne(Long id);

    /**
     * Delete the "id" corporateTenant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
