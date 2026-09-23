package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.WebhookSubscription;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WebhookSubscription entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WebhookSubscriptionRepository
    extends JpaRepository<WebhookSubscription, Long>, JpaSpecificationExecutor<WebhookSubscription> {}
