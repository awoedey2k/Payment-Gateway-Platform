package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.ApiKey;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApiKey entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long>, JpaSpecificationExecutor<ApiKey> {}
