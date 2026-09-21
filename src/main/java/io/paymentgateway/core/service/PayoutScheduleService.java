package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.PayoutSchedule}.
 */
public interface PayoutScheduleService {
    /**
     * Save a payoutSchedule.
     *
     * @param payoutScheduleDTO the entity to save.
     * @return the persisted entity.
     */
    PayoutScheduleDTO save(PayoutScheduleDTO payoutScheduleDTO);

    /**
     * Updates a payoutSchedule.
     *
     * @param payoutScheduleDTO the entity to update.
     * @return the persisted entity.
     */
    PayoutScheduleDTO update(PayoutScheduleDTO payoutScheduleDTO);

    /**
     * Partially updates a payoutSchedule.
     *
     * @param payoutScheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PayoutScheduleDTO> partialUpdate(PayoutScheduleDTO payoutScheduleDTO);

    /**
     * Get the "id" payoutSchedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PayoutScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" payoutSchedule.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
