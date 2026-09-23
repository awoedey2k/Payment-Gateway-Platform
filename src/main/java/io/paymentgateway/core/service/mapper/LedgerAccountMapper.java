package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.LedgerAccount;
import io.paymentgateway.core.service.dto.LedgerAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LedgerAccount} and its DTO {@link LedgerAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface LedgerAccountMapper extends EntityMapper<LedgerAccountDTO, LedgerAccount> {}
