package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.AuditLogEntry;
import io.paymentgateway.core.repository.AuditLogEntryRepository;
import io.paymentgateway.core.service.AuditLogEntryService;
import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
import io.paymentgateway.core.service.mapper.AuditLogEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.AuditLogEntry}.
 */
@Service
@Transactional
public class AuditLogEntryServiceImpl implements AuditLogEntryService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogEntryServiceImpl.class);

    private final AuditLogEntryRepository auditLogEntryRepository;

    private final AuditLogEntryMapper auditLogEntryMapper;

    public AuditLogEntryServiceImpl(AuditLogEntryRepository auditLogEntryRepository, AuditLogEntryMapper auditLogEntryMapper) {
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.auditLogEntryMapper = auditLogEntryMapper;
    }

    @Override
    public AuditLogEntryDTO save(AuditLogEntryDTO auditLogEntryDTO) {
        LOG.debug("Request to save AuditLogEntry : {}", auditLogEntryDTO);
        AuditLogEntry auditLogEntry = auditLogEntryMapper.toEntity(auditLogEntryDTO);
        auditLogEntry = auditLogEntryRepository.save(auditLogEntry);
        return auditLogEntryMapper.toDto(auditLogEntry);
    }

    @Override
    public AuditLogEntryDTO update(AuditLogEntryDTO auditLogEntryDTO) {
        LOG.debug("Request to update AuditLogEntry : {}", auditLogEntryDTO);
        AuditLogEntry auditLogEntry = auditLogEntryMapper.toEntity(auditLogEntryDTO);
        auditLogEntry = auditLogEntryRepository.save(auditLogEntry);
        return auditLogEntryMapper.toDto(auditLogEntry);
    }

    @Override
    public Optional<AuditLogEntryDTO> partialUpdate(AuditLogEntryDTO auditLogEntryDTO) {
        LOG.debug("Request to partially update AuditLogEntry : {}", auditLogEntryDTO);

        return auditLogEntryRepository
            .findById(auditLogEntryDTO.getId())
            .map(existingAuditLogEntry -> {
                auditLogEntryMapper.partialUpdate(existingAuditLogEntry, auditLogEntryDTO);

                return existingAuditLogEntry;
            })
            .map(auditLogEntryRepository::save)
            .map(auditLogEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditLogEntryDTO> findOne(Long id) {
        LOG.debug("Request to get AuditLogEntry : {}", id);
        return auditLogEntryRepository.findById(id).map(auditLogEntryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AuditLogEntry : {}", id);
        auditLogEntryRepository.deleteById(id);
    }
}
