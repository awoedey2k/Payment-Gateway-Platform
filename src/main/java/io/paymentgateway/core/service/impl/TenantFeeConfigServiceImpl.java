package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.TenantFeeConfig;
import io.paymentgateway.core.repository.TenantFeeConfigRepository;
import io.paymentgateway.core.service.TenantFeeConfigService;
import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
import io.paymentgateway.core.service.mapper.TenantFeeConfigMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.TenantFeeConfig}.
 */
@Service
@Transactional
public class TenantFeeConfigServiceImpl implements TenantFeeConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantFeeConfigServiceImpl.class);

    private final TenantFeeConfigRepository tenantFeeConfigRepository;

    private final TenantFeeConfigMapper tenantFeeConfigMapper;

    public TenantFeeConfigServiceImpl(TenantFeeConfigRepository tenantFeeConfigRepository, TenantFeeConfigMapper tenantFeeConfigMapper) {
        this.tenantFeeConfigRepository = tenantFeeConfigRepository;
        this.tenantFeeConfigMapper = tenantFeeConfigMapper;
    }

    @Override
    public TenantFeeConfigDTO save(TenantFeeConfigDTO tenantFeeConfigDTO) {
        LOG.debug("Request to save TenantFeeConfig : {}", tenantFeeConfigDTO);
        TenantFeeConfig tenantFeeConfig = tenantFeeConfigMapper.toEntity(tenantFeeConfigDTO);
        tenantFeeConfig = tenantFeeConfigRepository.save(tenantFeeConfig);
        return tenantFeeConfigMapper.toDto(tenantFeeConfig);
    }

    @Override
    public TenantFeeConfigDTO update(TenantFeeConfigDTO tenantFeeConfigDTO) {
        LOG.debug("Request to update TenantFeeConfig : {}", tenantFeeConfigDTO);
        TenantFeeConfig tenantFeeConfig = tenantFeeConfigMapper.toEntity(tenantFeeConfigDTO);
        tenantFeeConfig = tenantFeeConfigRepository.save(tenantFeeConfig);
        return tenantFeeConfigMapper.toDto(tenantFeeConfig);
    }

    @Override
    public Optional<TenantFeeConfigDTO> partialUpdate(TenantFeeConfigDTO tenantFeeConfigDTO) {
        LOG.debug("Request to partially update TenantFeeConfig : {}", tenantFeeConfigDTO);

        return tenantFeeConfigRepository
            .findById(tenantFeeConfigDTO.getId())
            .map(existingTenantFeeConfig -> {
                tenantFeeConfigMapper.partialUpdate(existingTenantFeeConfig, tenantFeeConfigDTO);

                return existingTenantFeeConfig;
            })
            .map(tenantFeeConfigRepository::save)
            .map(tenantFeeConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantFeeConfigDTO> findOne(Long id) {
        LOG.debug("Request to get TenantFeeConfig : {}", id);
        return tenantFeeConfigRepository.findById(id).map(tenantFeeConfigMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TenantFeeConfig : {}", id);
        tenantFeeConfigRepository.deleteById(id);
    }
}
