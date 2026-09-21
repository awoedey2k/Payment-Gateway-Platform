package io.paymentgateway.core.service.mapper;

import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.domain.DisputeEvidence;
import io.paymentgateway.core.service.dto.DisputeDTO;
import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DisputeEvidence} and its DTO {@link DisputeEvidenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface DisputeEvidenceMapper extends EntityMapper<DisputeEvidenceDTO, DisputeEvidence> {
    @Mapping(target = "dispute", source = "dispute", qualifiedByName = "disputeId")
    DisputeEvidenceDTO toDto(DisputeEvidence s);

    @Named("disputeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DisputeDTO toDtoDisputeId(Dispute dispute);
}
