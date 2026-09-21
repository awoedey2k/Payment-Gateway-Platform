package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantDirector;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.TenantDirectorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TenantDirector} and its DTO {@link TenantDirectorDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantDirectorMapper extends EntityMapper<TenantDirectorDTO, TenantDirector> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    TenantDirectorDTO toDto(TenantDirector s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
