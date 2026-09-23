package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.TenantDirector;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TenantDirector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantDirectorRepository extends JpaRepository<TenantDirector, Long>, JpaSpecificationExecutor<TenantDirector> {}
