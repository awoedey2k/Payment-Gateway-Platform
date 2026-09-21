package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.JournalLine;
import io.paymentgateway.core.repository.JournalLineRepository;
import io.paymentgateway.core.service.JournalLineService;
import io.paymentgateway.core.service.dto.JournalLineDTO;
import io.paymentgateway.core.service.mapper.JournalLineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.JournalLine}.
 */
@Service
@Transactional
public class JournalLineServiceImpl implements JournalLineService {

    private static final Logger LOG = LoggerFactory.getLogger(JournalLineServiceImpl.class);

    private final JournalLineRepository journalLineRepository;

    private final JournalLineMapper journalLineMapper;

    public JournalLineServiceImpl(JournalLineRepository journalLineRepository, JournalLineMapper journalLineMapper) {
        this.journalLineRepository = journalLineRepository;
        this.journalLineMapper = journalLineMapper;
    }

    @Override
    public JournalLineDTO save(JournalLineDTO journalLineDTO) {
        LOG.debug("Request to save JournalLine : {}", journalLineDTO);
        JournalLine journalLine = journalLineMapper.toEntity(journalLineDTO);
        journalLine = journalLineRepository.save(journalLine);
        return journalLineMapper.toDto(journalLine);
    }

    @Override
    public JournalLineDTO update(JournalLineDTO journalLineDTO) {
        LOG.debug("Request to update JournalLine : {}", journalLineDTO);
        JournalLine journalLine = journalLineMapper.toEntity(journalLineDTO);
        journalLine = journalLineRepository.save(journalLine);
        return journalLineMapper.toDto(journalLine);
    }

    @Override
    public Optional<JournalLineDTO> partialUpdate(JournalLineDTO journalLineDTO) {
        LOG.debug("Request to partially update JournalLine : {}", journalLineDTO);

        return journalLineRepository
            .findById(journalLineDTO.getId())
            .map(existingJournalLine -> {
                journalLineMapper.partialUpdate(existingJournalLine, journalLineDTO);

                return existingJournalLine;
            })
            .map(journalLineRepository::save)
            .map(journalLineMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JournalLineDTO> findOne(Long id) {
        LOG.debug("Request to get JournalLine : {}", id);
        return journalLineRepository.findById(id).map(journalLineMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete JournalLine : {}", id);
        journalLineRepository.deleteById(id);
    }
}
