package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.LedgerAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LedgerAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long>, JpaSpecificationExecutor<LedgerAccount> {}
