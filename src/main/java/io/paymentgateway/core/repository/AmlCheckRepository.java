package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.AmlCheck;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AmlCheck entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AmlCheckRepository extends JpaRepository<AmlCheck, Long>, JpaSpecificationExecutor<AmlCheck> {}
