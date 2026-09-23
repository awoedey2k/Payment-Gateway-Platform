package io.paymentgateway.core.extended.tenant.repository;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Extra queries for {@link CorporateTenant}. Deliberately does not extend the generated {@code CorporateTenantRepository}:
 * two beans of that type would make injection of the generated repository ambiguous.
 */
public interface ExtendedCorporateTenantRepository extends JpaRepository<CorporateTenant, Long> {
    /**
     * Loads the tenant with {@code SELECT ... FOR UPDATE}. All state changes that must be serialised per tenant (status
     * transitions, KYC decisions, key issue/rotate/revoke) start here, so concurrent operations on one tenant queue up
     * while other tenants are unaffected.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from CorporateTenant t where t.id = :id")
    Optional<CorporateTenant> findByIdForUpdate(@Param("id") Long id);

    /** Current status straight from the database (a query, so never a possibly stale cached entity). */
    @Query("select t.status from CorporateTenant t where t.id = :id")
    Optional<TenantStatus> findStatusById(@Param("id") Long id);
}
