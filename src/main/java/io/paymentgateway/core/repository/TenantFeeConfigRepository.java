package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.TenantFeeConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TenantFeeConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantFeeConfigRepository extends JpaRepository<TenantFeeConfig, Long>, JpaSpecificationExecutor<TenantFeeConfig> {}
