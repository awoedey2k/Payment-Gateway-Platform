package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.SettlementBatch;
import io.paymentgateway.core.repository.SettlementBatchRepository;
import io.paymentgateway.core.service.SettlementBatchService;
import io.paymentgateway.core.service.dto.SettlementBatchDTO;
import io.paymentgateway.core.service.mapper.SettlementBatchMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.SettlementBatch}.
 */
@Service
@Transactional
public class SettlementBatchServiceImpl implements SettlementBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(SettlementBatchServiceImpl.class);

    private final SettlementBatchRepository settlementBatchRepository;

    private final SettlementBatchMapper settlementBatchMapper;

    public SettlementBatchServiceImpl(SettlementBatchRepository settlementBatchRepository, SettlementBatchMapper settlementBatchMapper) {
        this.settlementBatchRepository = settlementBatchRepository;
        this.settlementBatchMapper = settlementBatchMapper;
    }

    @Override
    public SettlementBatchDTO save(SettlementBatchDTO settlementBatchDTO) {
        LOG.debug("Request to save SettlementBatch : {}", settlementBatchDTO);
        SettlementBatch settlementBatch = settlementBatchMapper.toEntity(settlementBatchDTO);
        settlementBatch = settlementBatchRepository.save(settlementBatch);
        return settlementBatchMapper.toDto(settlementBatch);
    }

    @Override
    public SettlementBatchDTO update(SettlementBatchDTO settlementBatchDTO) {
        LOG.debug("Request to update SettlementBatch : {}", settlementBatchDTO);
        SettlementBatch settlementBatch = settlementBatchMapper.toEntity(settlementBatchDTO);
        settlementBatch = settlementBatchRepository.save(settlementBatch);
        return settlementBatchMapper.toDto(settlementBatch);
    }

    @Override
    public Optional<SettlementBatchDTO> partialUpdate(SettlementBatchDTO settlementBatchDTO) {
        LOG.debug("Request to partially update SettlementBatch : {}", settlementBatchDTO);

        return settlementBatchRepository
            .findById(settlementBatchDTO.getId())
            .map(existingSettlementBatch -> {
                settlementBatchMapper.partialUpdate(existingSettlementBatch, settlementBatchDTO);

                return existingSettlementBatch;
            })
            .map(settlementBatchRepository::save)
            .map(settlementBatchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettlementBatchDTO> findOne(Long id) {
        LOG.debug("Request to get SettlementBatch : {}", id);
        return settlementBatchRepository.findById(id).map(settlementBatchMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SettlementBatch : {}", id);
        settlementBatchRepository.deleteById(id);
    }
}
