package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.service.dto.ApiKeyDTO;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApiKey} and its DTO {@link ApiKeyDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApiKeyMapper extends EntityMapper<ApiKeyDTO, ApiKey> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    ApiKeyDTO toDto(ApiKey s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
