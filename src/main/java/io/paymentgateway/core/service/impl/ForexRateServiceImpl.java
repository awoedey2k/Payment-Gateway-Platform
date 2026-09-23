package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.ForexRate;
import io.paymentgateway.core.repository.ForexRateRepository;
import io.paymentgateway.core.service.ForexRateService;
import io.paymentgateway.core.service.dto.ForexRateDTO;
import io.paymentgateway.core.service.mapper.ForexRateMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.ForexRate}.
 */
@Service
@Transactional
public class ForexRateServiceImpl implements ForexRateService {

    private static final Logger LOG = LoggerFactory.getLogger(ForexRateServiceImpl.class);

    private final ForexRateRepository forexRateRepository;

    private final ForexRateMapper forexRateMapper;

    public ForexRateServiceImpl(ForexRateRepository forexRateRepository, ForexRateMapper forexRateMapper) {
        this.forexRateRepository = forexRateRepository;
        this.forexRateMapper = forexRateMapper;
    }

    @Override
    public ForexRateDTO save(ForexRateDTO forexRateDTO) {
        LOG.debug("Request to save ForexRate : {}", forexRateDTO);
        ForexRate forexRate = forexRateMapper.toEntity(forexRateDTO);
        forexRate = forexRateRepository.save(forexRate);
        return forexRateMapper.toDto(forexRate);
    }

    @Override
    public ForexRateDTO update(ForexRateDTO forexRateDTO) {
        LOG.debug("Request to update ForexRate : {}", forexRateDTO);
        ForexRate forexRate = forexRateMapper.toEntity(forexRateDTO);
        forexRate = forexRateRepository.save(forexRate);
        return forexRateMapper.toDto(forexRate);
    }

    @Override
    public Optional<ForexRateDTO> partialUpdate(ForexRateDTO forexRateDTO) {
        LOG.debug("Request to partially update ForexRate : {}", forexRateDTO);

        return forexRateRepository
            .findById(forexRateDTO.getId())
            .map(existingForexRate -> {
                forexRateMapper.partialUpdate(existingForexRate, forexRateDTO);

                return existingForexRate;
            })
            .map(forexRateRepository::save)
            .map(forexRateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ForexRateDTO> findOne(Long id) {
        LOG.debug("Request to get ForexRate : {}", id);
        return forexRateRepository.findById(id).map(forexRateMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ForexRate : {}", id);
        forexRateRepository.deleteById(id);
    }
}
