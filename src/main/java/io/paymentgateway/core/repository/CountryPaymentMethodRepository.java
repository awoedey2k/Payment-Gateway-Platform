package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.CountryPaymentMethod;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CountryPaymentMethod entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CountryPaymentMethodRepository
    extends JpaRepository<CountryPaymentMethod, Long>, JpaSpecificationExecutor<CountryPaymentMethod> {}
