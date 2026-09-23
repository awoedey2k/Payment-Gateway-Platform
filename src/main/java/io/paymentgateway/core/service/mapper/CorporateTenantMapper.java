package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CorporateTenant} and its DTO {@link CorporateTenantDTO}.
 */
@Mapper(componentModel = "spring")
public interface CorporateTenantMapper extends EntityMapper<CorporateTenantDTO, CorporateTenant> {}
