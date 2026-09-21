package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.SettlementBatch;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.SettlementBatchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SettlementBatch} and its DTO {@link SettlementBatchDTO}.
 */
@Mapper(componentModel = "spring")
public interface SettlementBatchMapper extends EntityMapper<SettlementBatchDTO, SettlementBatch> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    SettlementBatchDTO toDto(SettlementBatch s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
