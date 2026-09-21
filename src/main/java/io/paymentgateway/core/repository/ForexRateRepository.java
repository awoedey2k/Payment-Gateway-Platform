package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.ForexRate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ForexRate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ForexRateRepository extends JpaRepository<ForexRate, Long>, JpaSpecificationExecutor<ForexRate> {}
