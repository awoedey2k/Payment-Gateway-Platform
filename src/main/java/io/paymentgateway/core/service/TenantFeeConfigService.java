package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.TenantFeeConfig}.
 */
public interface TenantFeeConfigService {
    /**
     * Save a tenantFeeConfig.
     *
     * @param tenantFeeConfigDTO the entity to save.
     * @return the persisted entity.
     */
    TenantFeeConfigDTO save(TenantFeeConfigDTO tenantFeeConfigDTO);

    /**
     * Updates a tenantFeeConfig.
     *
     * @param tenantFeeConfigDTO the entity to update.
     * @return the persisted entity.
     */
    TenantFeeConfigDTO update(TenantFeeConfigDTO tenantFeeConfigDTO);

    /**
     * Partially updates a tenantFeeConfig.
     *
     * @param tenantFeeConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TenantFeeConfigDTO> partialUpdate(TenantFeeConfigDTO tenantFeeConfigDTO);

    /**
     * Get the "id" tenantFeeConfig.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TenantFeeConfigDTO> findOne(Long id);

    /**
     * Delete the "id" tenantFeeConfig.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
