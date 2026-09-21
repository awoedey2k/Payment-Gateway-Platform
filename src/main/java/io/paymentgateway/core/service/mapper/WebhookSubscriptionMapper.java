package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WebhookSubscription} and its DTO {@link WebhookSubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface WebhookSubscriptionMapper extends EntityMapper<WebhookSubscriptionDTO, WebhookSubscription> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    WebhookSubscriptionDTO toDto(WebhookSubscription s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
