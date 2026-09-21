package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.TenantDomainDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TenantDomain} and its DTO {@link TenantDomainDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantDomainMapper extends EntityMapper<TenantDomainDTO, TenantDomain> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    TenantDomainDTO toDto(TenantDomain s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
