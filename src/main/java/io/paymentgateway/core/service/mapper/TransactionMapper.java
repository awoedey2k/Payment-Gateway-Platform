package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    TransactionDTO toDto(Transaction s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
