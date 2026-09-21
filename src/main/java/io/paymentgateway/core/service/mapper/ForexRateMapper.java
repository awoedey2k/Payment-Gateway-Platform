package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.ForexRate;
import io.paymentgateway.core.service.dto.ForexRateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ForexRate} and its DTO {@link ForexRateDTO}.
 */
@Mapper(componentModel = "spring")
public interface ForexRateMapper extends EntityMapper<ForexRateDTO, ForexRate> {}
