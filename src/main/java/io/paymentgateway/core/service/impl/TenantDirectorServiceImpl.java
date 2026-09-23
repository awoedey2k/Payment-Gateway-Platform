package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.TenantDirector;
import io.paymentgateway.core.repository.TenantDirectorRepository;
import io.paymentgateway.core.service.TenantDirectorService;
import io.paymentgateway.core.service.dto.TenantDirectorDTO;
import io.paymentgateway.core.service.mapper.TenantDirectorMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.TenantDirector}.
 */
@Service
@Transactional
public class TenantDirectorServiceImpl implements TenantDirectorService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDirectorServiceImpl.class);

    private final TenantDirectorRepository tenantDirectorRepository;

    private final TenantDirectorMapper tenantDirectorMapper;

    public TenantDirectorServiceImpl(TenantDirectorRepository tenantDirectorRepository, TenantDirectorMapper tenantDirectorMapper) {
        this.tenantDirectorRepository = tenantDirectorRepository;
        this.tenantDirectorMapper = tenantDirectorMapper;
    }

    @Override
    public TenantDirectorDTO save(TenantDirectorDTO tenantDirectorDTO) {
        LOG.debug("Request to save TenantDirector : {}", tenantDirectorDTO);
        TenantDirector tenantDirector = tenantDirectorMapper.toEntity(tenantDirectorDTO);
        tenantDirector = tenantDirectorRepository.save(tenantDirector);
        return tenantDirectorMapper.toDto(tenantDirector);
    }

    @Override
    public TenantDirectorDTO update(TenantDirectorDTO tenantDirectorDTO) {
        LOG.debug("Request to update TenantDirector : {}", tenantDirectorDTO);
        TenantDirector tenantDirector = tenantDirectorMapper.toEntity(tenantDirectorDTO);
        tenantDirector = tenantDirectorRepository.save(tenantDirector);
        return tenantDirectorMapper.toDto(tenantDirector);
    }

    @Override
    public Optional<TenantDirectorDTO> partialUpdate(TenantDirectorDTO tenantDirectorDTO) {
        LOG.debug("Request to partially update TenantDirector : {}", tenantDirectorDTO);

        return tenantDirectorRepository
            .findById(tenantDirectorDTO.getId())
            .map(existingTenantDirector -> {
                tenantDirectorMapper.partialUpdate(existingTenantDirector, tenantDirectorDTO);

                return existingTenantDirector;
            })
            .map(tenantDirectorRepository::save)
            .map(tenantDirectorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantDirectorDTO> findOne(Long id) {
        LOG.debug("Request to get TenantDirector : {}", id);
        return tenantDirectorRepository.findById(id).map(tenantDirectorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TenantDirector : {}", id);
        tenantDirectorRepository.deleteById(id);
    }
}
