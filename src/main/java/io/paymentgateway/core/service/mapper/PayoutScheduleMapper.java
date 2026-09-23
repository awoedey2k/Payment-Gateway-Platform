package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.PayoutSchedule;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.PayoutScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PayoutSchedule} and its DTO {@link PayoutScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface PayoutScheduleMapper extends EntityMapper<PayoutScheduleDTO, PayoutSchedule> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    PayoutScheduleDTO toDto(PayoutSchedule s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
