package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.RoutingRule;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoutingRule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoutingRuleRepository extends JpaRepository<RoutingRule, Long>, JpaSpecificationExecutor<RoutingRule> {}
