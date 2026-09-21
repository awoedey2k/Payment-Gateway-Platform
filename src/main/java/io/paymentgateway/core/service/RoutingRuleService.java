package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.RoutingRuleDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.RoutingRule}.
 */
public interface RoutingRuleService {
    /**
     * Save a routingRule.
     *
     * @param routingRuleDTO the entity to save.
     * @return the persisted entity.
     */
    RoutingRuleDTO save(RoutingRuleDTO routingRuleDTO);

    /**
     * Updates a routingRule.
     *
     * @param routingRuleDTO the entity to update.
     * @return the persisted entity.
     */
    RoutingRuleDTO update(RoutingRuleDTO routingRuleDTO);

    /**
     * Partially updates a routingRule.
     *
     * @param routingRuleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RoutingRuleDTO> partialUpdate(RoutingRuleDTO routingRuleDTO);

    /**
     * Get the "id" routingRule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RoutingRuleDTO> findOne(Long id);

    /**
     * Delete the "id" routingRule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
