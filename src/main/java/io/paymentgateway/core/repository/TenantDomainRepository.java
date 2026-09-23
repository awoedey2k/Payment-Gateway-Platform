package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.TenantDomain;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TenantDomain entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantDomainRepository extends JpaRepository<TenantDomain, Long>, JpaSpecificationExecutor<TenantDomain> {}
