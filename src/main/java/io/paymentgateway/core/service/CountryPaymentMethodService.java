package io.paymentgateway.core.service;

import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link io.paymentgateway.core.domain.CountryPaymentMethod}.
 */
public interface CountryPaymentMethodService {
    /**
     * Save a countryPaymentMethod.
     *
     * @param countryPaymentMethodDTO the entity to save.
     * @return the persisted entity.
     */
    CountryPaymentMethodDTO save(CountryPaymentMethodDTO countryPaymentMethodDTO);

    /**
     * Updates a countryPaymentMethod.
     *
     * @param countryPaymentMethodDTO the entity to update.
     * @return the persisted entity.
     */
    CountryPaymentMethodDTO update(CountryPaymentMethodDTO countryPaymentMethodDTO);

    /**
     * Partially updates a countryPaymentMethod.
     *
     * @param countryPaymentMethodDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CountryPaymentMethodDTO> partialUpdate(CountryPaymentMethodDTO countryPaymentMethodDTO);

    /**
     * Get the "id" countryPaymentMethod.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CountryPaymentMethodDTO> findOne(Long id);

    /**
     * Delete the "id" countryPaymentMethod.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
