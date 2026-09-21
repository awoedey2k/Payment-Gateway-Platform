package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.repository.TenantDomainRepository;
import io.paymentgateway.core.service.TenantDomainService;
import io.paymentgateway.core.service.dto.TenantDomainDTO;
import io.paymentgateway.core.service.mapper.TenantDomainMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.TenantDomain}.
 */
@Service
@Transactional
public class TenantDomainServiceImpl implements TenantDomainService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDomainServiceImpl.class);

    private final TenantDomainRepository tenantDomainRepository;

    private final TenantDomainMapper tenantDomainMapper;

    public TenantDomainServiceImpl(TenantDomainRepository tenantDomainRepository, TenantDomainMapper tenantDomainMapper) {
        this.tenantDomainRepository = tenantDomainRepository;
        this.tenantDomainMapper = tenantDomainMapper;
    }

    @Override
    public TenantDomainDTO save(TenantDomainDTO tenantDomainDTO) {
        LOG.debug("Request to save TenantDomain : {}", tenantDomainDTO);
        TenantDomain tenantDomain = tenantDomainMapper.toEntity(tenantDomainDTO);
        tenantDomain = tenantDomainRepository.save(tenantDomain);
        return tenantDomainMapper.toDto(tenantDomain);
    }

    @Override
    public TenantDomainDTO update(TenantDomainDTO tenantDomainDTO) {
        LOG.debug("Request to update TenantDomain : {}", tenantDomainDTO);
        TenantDomain tenantDomain = tenantDomainMapper.toEntity(tenantDomainDTO);
        tenantDomain = tenantDomainRepository.save(tenantDomain);
        return tenantDomainMapper.toDto(tenantDomain);
    }

    @Override
    public Optional<TenantDomainDTO> partialUpdate(TenantDomainDTO tenantDomainDTO) {
        LOG.debug("Request to partially update TenantDomain : {}", tenantDomainDTO);

        return tenantDomainRepository
            .findById(tenantDomainDTO.getId())
            .map(existingTenantDomain -> {
                tenantDomainMapper.partialUpdate(existingTenantDomain, tenantDomainDTO);

                return existingTenantDomain;
            })
            .map(tenantDomainRepository::save)
            .map(tenantDomainMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantDomainDTO> findOne(Long id) {
        LOG.debug("Request to get TenantDomain : {}", id);
        return tenantDomainRepository.findById(id).map(tenantDomainMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TenantDomain : {}", id);
        tenantDomainRepository.deleteById(id);
    }
}
