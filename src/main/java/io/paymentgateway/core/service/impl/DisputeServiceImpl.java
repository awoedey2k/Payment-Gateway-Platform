package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.Dispute;
import io.paymentgateway.core.repository.DisputeRepository;
import io.paymentgateway.core.service.DisputeService;
import io.paymentgateway.core.service.dto.DisputeDTO;
import io.paymentgateway.core.service.mapper.DisputeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.Dispute}.
 */
@Service
@Transactional
public class DisputeServiceImpl implements DisputeService {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeServiceImpl.class);

    private final DisputeRepository disputeRepository;

    private final DisputeMapper disputeMapper;

    public DisputeServiceImpl(DisputeRepository disputeRepository, DisputeMapper disputeMapper) {
        this.disputeRepository = disputeRepository;
        this.disputeMapper = disputeMapper;
    }

    @Override
    public DisputeDTO save(DisputeDTO disputeDTO) {
        LOG.debug("Request to save Dispute : {}", disputeDTO);
        Dispute dispute = disputeMapper.toEntity(disputeDTO);
        dispute = disputeRepository.save(dispute);
        return disputeMapper.toDto(dispute);
    }

    @Override
    public DisputeDTO update(DisputeDTO disputeDTO) {
        LOG.debug("Request to update Dispute : {}", disputeDTO);
        Dispute dispute = disputeMapper.toEntity(disputeDTO);
        dispute = disputeRepository.save(dispute);
        return disputeMapper.toDto(dispute);
    }

    @Override
    public Optional<DisputeDTO> partialUpdate(DisputeDTO disputeDTO) {
        LOG.debug("Request to partially update Dispute : {}", disputeDTO);

        return disputeRepository
            .findById(disputeDTO.getId())
            .map(existingDispute -> {
                disputeMapper.partialUpdate(existingDispute, disputeDTO);

                return existingDispute;
            })
            .map(disputeRepository::save)
            .map(disputeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DisputeDTO> findOne(Long id) {
        LOG.debug("Request to get Dispute : {}", id);
        return disputeRepository.findById(id).map(disputeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Dispute : {}", id);
        disputeRepository.deleteById(id);
    }
}
