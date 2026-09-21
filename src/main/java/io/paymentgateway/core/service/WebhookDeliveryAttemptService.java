package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.WebhookDeliveryAttempt}.
 */
public interface WebhookDeliveryAttemptService {
    /**
     * Save a webhookDeliveryAttempt.
     *
     * @param webhookDeliveryAttemptDTO the entity to save.
     * @return the persisted entity.
     */
    WebhookDeliveryAttemptDTO save(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO);

    /**
     * Updates a webhookDeliveryAttempt.
     *
     * @param webhookDeliveryAttemptDTO the entity to update.
     * @return the persisted entity.
     */
    WebhookDeliveryAttemptDTO update(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO);

    /**
     * Partially updates a webhookDeliveryAttempt.
     *
     * @param webhookDeliveryAttemptDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WebhookDeliveryAttemptDTO> partialUpdate(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO);

    /**
     * Get the "id" webhookDeliveryAttempt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WebhookDeliveryAttemptDTO> findOne(Long id);

    /**
     * Delete the "id" webhookDeliveryAttempt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
