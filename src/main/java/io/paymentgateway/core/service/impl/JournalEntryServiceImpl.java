package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.JournalEntry;
import io.paymentgateway.core.repository.JournalEntryRepository;
import io.paymentgateway.core.service.JournalEntryService;
import io.paymentgateway.core.service.dto.JournalEntryDTO;
import io.paymentgateway.core.service.mapper.JournalEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.JournalEntry}.
 */
@Service
@Transactional
public class JournalEntryServiceImpl implements JournalEntryService {

    private static final Logger LOG = LoggerFactory.getLogger(JournalEntryServiceImpl.class);

    private final JournalEntryRepository journalEntryRepository;

    private final JournalEntryMapper journalEntryMapper;

    public JournalEntryServiceImpl(JournalEntryRepository journalEntryRepository, JournalEntryMapper journalEntryMapper) {
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryMapper = journalEntryMapper;
    }

    @Override
    public JournalEntryDTO save(JournalEntryDTO journalEntryDTO) {
        LOG.debug("Request to save JournalEntry : {}", journalEntryDTO);
        JournalEntry journalEntry = journalEntryMapper.toEntity(journalEntryDTO);
        journalEntry = journalEntryRepository.save(journalEntry);
        return journalEntryMapper.toDto(journalEntry);
    }

    @Override
    public JournalEntryDTO update(JournalEntryDTO journalEntryDTO) {
        LOG.debug("Request to update JournalEntry : {}", journalEntryDTO);
        JournalEntry journalEntry = journalEntryMapper.toEntity(journalEntryDTO);
        journalEntry = journalEntryRepository.save(journalEntry);
        return journalEntryMapper.toDto(journalEntry);
    }

    @Override
    public Optional<JournalEntryDTO> partialUpdate(JournalEntryDTO journalEntryDTO) {
        LOG.debug("Request to partially update JournalEntry : {}", journalEntryDTO);

        return journalEntryRepository
            .findById(journalEntryDTO.getId())
            .map(existingJournalEntry -> {
                journalEntryMapper.partialUpdate(existingJournalEntry, journalEntryDTO);

                return existingJournalEntry;
            })
            .map(journalEntryRepository::save)
            .map(journalEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JournalEntryDTO> findOne(Long id) {
        LOG.debug("Request to get JournalEntry : {}", id);
        return journalEntryRepository.findById(id).map(journalEntryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete JournalEntry : {}", id);
        journalEntryRepository.deleteById(id);
    }
}
