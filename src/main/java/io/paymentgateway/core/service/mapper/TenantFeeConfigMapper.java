package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.domain.TenantFeeConfig;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TenantFeeConfig} and its DTO {@link TenantFeeConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface TenantFeeConfigMapper extends EntityMapper<TenantFeeConfigDTO, TenantFeeConfig> {
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "corporateTenantId")
    @Mapping(target = "countryPaymentMethod", source = "countryPaymentMethod", qualifiedByName = "countryPaymentMethodId")
    TenantFeeConfigDTO toDto(TenantFeeConfig s);

    @Named("corporateTenantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CorporateTenantDTO toDtoCorporateTenantId(CorporateTenant corporateTenant);

    @Named("countryPaymentMethodId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryPaymentMethodDTO toDtoCountryPaymentMethodId(CountryPaymentMethod countryPaymentMethod);
}
