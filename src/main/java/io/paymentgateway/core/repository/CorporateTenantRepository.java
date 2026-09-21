package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.CorporateTenant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CorporateTenant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CorporateTenantRepository extends JpaRepository<CorporateTenant, Long>, JpaSpecificationExecutor<CorporateTenant> {}
