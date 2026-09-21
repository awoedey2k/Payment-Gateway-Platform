package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.TenantWallet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TenantWallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantWalletRepository extends JpaRepository<TenantWallet, Long>, JpaSpecificationExecutor<TenantWallet> {}
