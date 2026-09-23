package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.WebhookDeliveryAttempt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WebhookDeliveryAttempt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WebhookDeliveryAttemptRepository
    extends JpaRepository<WebhookDeliveryAttempt, Long>, JpaSpecificationExecutor<WebhookDeliveryAttempt> {}
