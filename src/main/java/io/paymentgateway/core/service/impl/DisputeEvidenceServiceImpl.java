package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.DisputeEvidence;
import io.paymentgateway.core.repository.DisputeEvidenceRepository;
import io.paymentgateway.core.service.DisputeEvidenceService;
import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import io.paymentgateway.core.service.mapper.DisputeEvidenceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.DisputeEvidence}.
 */
@Service
@Transactional
public class DisputeEvidenceServiceImpl implements DisputeEvidenceService {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeEvidenceServiceImpl.class);

    private final DisputeEvidenceRepository disputeEvidenceRepository;

    private final DisputeEvidenceMapper disputeEvidenceMapper;

    public DisputeEvidenceServiceImpl(DisputeEvidenceRepository disputeEvidenceRepository, DisputeEvidenceMapper disputeEvidenceMapper) {
        this.disputeEvidenceRepository = disputeEvidenceRepository;
        this.disputeEvidenceMapper = disputeEvidenceMapper;
    }

    @Override
    public DisputeEvidenceDTO save(DisputeEvidenceDTO disputeEvidenceDTO) {
        LOG.debug("Request to save DisputeEvidence : {}", disputeEvidenceDTO);
        DisputeEvidence disputeEvidence = disputeEvidenceMapper.toEntity(disputeEvidenceDTO);
        disputeEvidence = disputeEvidenceRepository.save(disputeEvidence);
        return disputeEvidenceMapper.toDto(disputeEvidence);
    }

    @Override
    public DisputeEvidenceDTO update(DisputeEvidenceDTO disputeEvidenceDTO) {
        LOG.debug("Request to update DisputeEvidence : {}", disputeEvidenceDTO);
        DisputeEvidence disputeEvidence = disputeEvidenceMapper.toEntity(disputeEvidenceDTO);
        disputeEvidence = disputeEvidenceRepository.save(disputeEvidence);
        return disputeEvidenceMapper.toDto(disputeEvidence);
    }

    @Override
    public Optional<DisputeEvidenceDTO> partialUpdate(DisputeEvidenceDTO disputeEvidenceDTO) {
        LOG.debug("Request to partially update DisputeEvidence : {}", disputeEvidenceDTO);

        return disputeEvidenceRepository
            .findById(disputeEvidenceDTO.getId())
            .map(existingDisputeEvidence -> {
                disputeEvidenceMapper.partialUpdate(existingDisputeEvidence, disputeEvidenceDTO);

                return existingDisputeEvidence;
            })
            .map(disputeEvidenceRepository::save)
            .map(disputeEvidenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisputeEvidenceDTO> findOne(Long id) {
        LOG.debug("Request to get DisputeEvidence : {}", id);
        return disputeEvidenceRepository.findById(id).map(disputeEvidenceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DisputeEvidence : {}", id);
        disputeEvidenceRepository.deleteById(id);
    }
}
