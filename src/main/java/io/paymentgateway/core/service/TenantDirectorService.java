package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.TenantDirectorDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.TenantDirector}.
 */
public interface TenantDirectorService {
    /**
     * Save a tenantDirector.
     *
     * @param tenantDirectorDTO the entity to save.
     * @return the persisted entity.
     */
    TenantDirectorDTO save(TenantDirectorDTO tenantDirectorDTO);

    /**
     * Updates a tenantDirector.
     *
     * @param tenantDirectorDTO the entity to update.
     * @return the persisted entity.
     */
    TenantDirectorDTO update(TenantDirectorDTO tenantDirectorDTO);

    /**
     * Partially updates a tenantDirector.
     *
     * @param tenantDirectorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TenantDirectorDTO> partialUpdate(TenantDirectorDTO tenantDirectorDTO);

    /**
     * Get the "id" tenantDirector.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TenantDirectorDTO> findOne(Long id);

    /**
     * Delete the "id" tenantDirector.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
