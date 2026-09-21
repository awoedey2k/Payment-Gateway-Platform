package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.WebhookSubscription}.
 */
public interface WebhookSubscriptionService {
    /**
     * Save a webhookSubscription.
     *
     * @param webhookSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    WebhookSubscriptionDTO save(WebhookSubscriptionDTO webhookSubscriptionDTO);

    /**
     * Updates a webhookSubscription.
     *
     * @param webhookSubscriptionDTO the entity to update.
     * @return the persisted entity.
     */
    WebhookSubscriptionDTO update(WebhookSubscriptionDTO webhookSubscriptionDTO);

    /**
     * Partially updates a webhookSubscription.
     *
     * @param webhookSubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WebhookSubscriptionDTO> partialUpdate(WebhookSubscriptionDTO webhookSubscriptionDTO);

    /**
     * Get the "id" webhookSubscription.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WebhookSubscriptionDTO> findOne(Long id);

    /**
     * Delete the "id" webhookSubscription.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
