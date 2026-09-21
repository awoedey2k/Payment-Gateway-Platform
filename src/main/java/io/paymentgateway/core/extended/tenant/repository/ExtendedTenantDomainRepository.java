package io.paymentgateway.core.extended.tenant.repository;

import io.paymentgateway.core.domain.TenantDomain;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtendedTenantDomainRepository extends JpaRepository<TenantDomain, Long> {
    @Query("select d from TenantDomain d join fetch d.tenant where lower(d.customDomain) = lower(:host)")
    Optional<TenantDomain> findByHost(@Param("host") String host);
}
