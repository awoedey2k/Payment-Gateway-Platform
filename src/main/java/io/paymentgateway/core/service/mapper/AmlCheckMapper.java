package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.AmlCheck;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.service.dto.AmlCheckDTO;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AmlCheck} and its DTO {@link AmlCheckDTO}.
 */
@Mapper(componentModel = "spring")
public interface AmlCheckMapper extends EntityMapper<AmlCheckDTO, AmlCheck> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    @Mapping(target = "transaction", source = "transaction", qualifiedByName = "transactionId")
    AmlCheckDTO toDto(AmlCheck s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);

    @Named("transactionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TransactionDTO toDtoTransactionId(Transaction transaction);
}
