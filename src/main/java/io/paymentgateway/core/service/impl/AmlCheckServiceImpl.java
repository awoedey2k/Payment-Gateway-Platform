package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.AmlCheck;
import io.paymentgateway.core.repository.AmlCheckRepository;
import io.paymentgateway.core.service.AmlCheckService;
import io.paymentgateway.core.service.dto.AmlCheckDTO;
import io.paymentgateway.core.service.mapper.AmlCheckMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.AmlCheck}.
 */
@Service
@Transactional
public class AmlCheckServiceImpl implements AmlCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(AmlCheckServiceImpl.class);

    private final AmlCheckRepository amlCheckRepository;

    private final AmlCheckMapper amlCheckMapper;

    public AmlCheckServiceImpl(AmlCheckRepository amlCheckRepository, AmlCheckMapper amlCheckMapper) {
        this.amlCheckRepository = amlCheckRepository;
        this.amlCheckMapper = amlCheckMapper;
    }

    @Override
    public AmlCheckDTO save(AmlCheckDTO amlCheckDTO) {
        LOG.debug("Request to save AmlCheck : {}", amlCheckDTO);
        AmlCheck amlCheck = amlCheckMapper.toEntity(amlCheckDTO);
        amlCheck = amlCheckRepository.save(amlCheck);
        return amlCheckMapper.toDto(amlCheck);
    }

    @Override
    public AmlCheckDTO update(AmlCheckDTO amlCheckDTO) {
        LOG.debug("Request to update AmlCheck : {}", amlCheckDTO);
        AmlCheck amlCheck = amlCheckMapper.toEntity(amlCheckDTO);
        amlCheck = amlCheckRepository.save(amlCheck);
        return amlCheckMapper.toDto(amlCheck);
    }

    @Override
    public Optional<AmlCheckDTO> partialUpdate(AmlCheckDTO amlCheckDTO) {
        LOG.debug("Request to partially update AmlCheck : {}", amlCheckDTO);

        return amlCheckRepository
            .findById(amlCheckDTO.getId())
            .map(existingAmlCheck -> {
                amlCheckMapper.partialUpdate(existingAmlCheck, amlCheckDTO);

                return existingAmlCheck;
            })
            .map(amlCheckRepository::save)
            .map(amlCheckMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AmlCheckDTO> findOne(Long id) {
        LOG.debug("Request to get AmlCheck : {}", id);
        return amlCheckRepository.findById(id).map(amlCheckMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AmlCheck : {}", id);
        amlCheckRepository.deleteById(id);
    }
}
