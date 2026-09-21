package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.PayoutSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PayoutSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PayoutScheduleRepository extends JpaRepository<PayoutSchedule, Long>, JpaSpecificationExecutor<PayoutSchedule> {}
