package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.RoutingRule;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.RoutingRuleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoutingRule} and its DTO {@link RoutingRuleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoutingRuleMapper extends EntityMapper<RoutingRuleDTO, RoutingRule> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    RoutingRuleDTO toDto(RoutingRule s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
