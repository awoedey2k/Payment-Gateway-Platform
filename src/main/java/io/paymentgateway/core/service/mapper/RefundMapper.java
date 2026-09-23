package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.Refund;
import io.paymentgateway.core.domain.Transaction;
import io.paymentgateway.core.service.dto.RefundDTO;
import io.paymentgateway.core.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refund} and its DTO {@link RefundDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundMapper extends EntityMapper<RefundDTO, Refund> {
    @Mapping(target = "transaction", source = "transaction", qualifiedByName = "transactionId")
    RefundDTO toDto(Refund s);

    @Named("transactionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TransactionDTO toDtoTransactionId(Transaction transaction);
}
