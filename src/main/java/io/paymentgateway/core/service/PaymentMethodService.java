package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.PaymentMethodDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.PaymentMethod}.
 */
public interface PaymentMethodService {
    /**
     * Save a paymentMethod.
     *
     * @param paymentMethodDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentMethodDTO save(PaymentMethodDTO paymentMethodDTO);

    /**
     * Updates a paymentMethod.
     *
     * @param paymentMethodDTO the entity to update.
     * @return the persisted entity.
     */
    PaymentMethodDTO update(PaymentMethodDTO paymentMethodDTO);

    /**
     * Partially updates a paymentMethod.
     *
     * @param paymentMethodDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaymentMethodDTO> partialUpdate(PaymentMethodDTO paymentMethodDTO);

    /**
     * Get the "id" paymentMethod.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaymentMethodDTO> findOne(Long id);

    /**
     * Delete the "id" paymentMethod.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
