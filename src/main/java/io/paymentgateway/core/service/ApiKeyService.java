package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.ApiKeyDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.ApiKey}.
 */
public interface ApiKeyService {
    /**
     * Save a apiKey.
     *
     * @param apiKeyDTO the entity to save.
     * @return the persisted entity.
     */
    ApiKeyDTO save(ApiKeyDTO apiKeyDTO);

    /**
     * Updates a apiKey.
     *
     * @param apiKeyDTO the entity to update.
     * @return the persisted entity.
     */
    ApiKeyDTO update(ApiKeyDTO apiKeyDTO);

    /**
     * Partially updates a apiKey.
     *
     * @param apiKeyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO);

    /**
     * Get the "id" apiKey.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ApiKeyDTO> findOne(Long id);

    /**
     * Delete the "id" apiKey.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
