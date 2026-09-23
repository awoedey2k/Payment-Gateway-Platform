package io.paymentgateway.core.extended.tenant.repository;

import io.paymentgateway.core.domain.TenantDirector;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtendedTenantDirectorRepository extends JpaRepository<TenantDirector, Long> {
    List<TenantDirector> findByTenantId(Long tenantId);
}
