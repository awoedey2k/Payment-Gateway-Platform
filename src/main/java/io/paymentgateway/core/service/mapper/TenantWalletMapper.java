package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.TenantWallet;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.TenantWalletDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TenantWallet} and its DTO {@link TenantWalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantWalletMapper extends EntityMapper<TenantWalletDTO, TenantWallet> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    TenantWalletDTO toDto(TenantWallet s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);
}
