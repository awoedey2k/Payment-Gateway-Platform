package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.Country;
import io.paymentgateway.core.domain.CountryPaymentMethod;
import io.paymentgateway.core.domain.PaymentMethod;
import io.paymentgateway.core.service.dto.CountryDTO;
import io.paymentgateway.core.service.dto.CountryPaymentMethodDTO;
import io.paymentgateway.core.service.dto.PaymentMethodDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CountryPaymentMethod} and its DTO {@link CountryPaymentMethodDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryPaymentMethodMapper extends EntityMapper<CountryPaymentMethodDTO, CountryPaymentMethod> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodId")
    CountryPaymentMethodDTO toDto(CountryPaymentMethod s);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);

    @Named("paymentMethodId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaymentMethodDTO toDtoPaymentMethodId(PaymentMethod paymentMethod);
}
