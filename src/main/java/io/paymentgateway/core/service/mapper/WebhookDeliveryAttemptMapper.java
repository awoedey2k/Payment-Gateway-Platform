package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.WebhookDeliveryAttempt;
import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WebhookDeliveryAttempt} and its DTO {@link WebhookDeliveryAttemptDTO}.
 */
@Mapper(componentModel = "spring")
public interface WebhookDeliveryAttemptMapper extends EntityMapper<WebhookDeliveryAttemptDTO, WebhookDeliveryAttempt> {
    @Mapping(target = "subscription", source = "subscription", qualifiedByName = "webhookSubscriptionId")
    WebhookDeliveryAttemptDTO toDto(WebhookDeliveryAttempt s);

    @Named("webhookSubscriptionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WebhookSubscriptionDTO toDtoWebhookSubscriptionId(WebhookSubscription webhookSubscription);
}
