package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.TenantDomainDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.TenantDomain}.
 */
public interface TenantDomainService {
    /**
     * Save a tenantDomain.
     *
     * @param tenantDomainDTO the entity to save.
     * @return the persisted entity.
     */
    TenantDomainDTO save(TenantDomainDTO tenantDomainDTO);

    /**
     * Updates a tenantDomain.
     *
     * @param tenantDomainDTO the entity to update.
     * @return the persisted entity.
     */
    TenantDomainDTO update(TenantDomainDTO tenantDomainDTO);

    /**
     * Partially updates a tenantDomain.
     *
     * @param tenantDomainDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TenantDomainDTO> partialUpdate(TenantDomainDTO tenantDomainDTO);

    /**
     * Get the "id" tenantDomain.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TenantDomainDTO> findOne(Long id);

    /**
     * Delete the "id" tenantDomain.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
