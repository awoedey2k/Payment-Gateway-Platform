package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.DisputeDTO;
import io.paymentgateway.core.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dispute} and its DTO {@link DisputeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DisputeMapper extends EntityMapper<DisputeDTO, Dispute> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    @Mapping(target = "transaction", source = "transaction", qualifiedByName = "transactionId")
    DisputeDTO toDto(Dispute s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);

    @Named("transactionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TransactionDTO toDtoTransactionId(Transaction transaction);
}
