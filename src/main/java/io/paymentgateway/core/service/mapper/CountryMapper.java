package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.Country;
import io.paymentgateway.core.service.dto.CountryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {}
