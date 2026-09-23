package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.TenantWallet;
import io.paymentgateway.core.repository.TenantWalletRepository;
import io.paymentgateway.core.service.TenantWalletService;
import io.paymentgateway.core.service.dto.TenantWalletDTO;
import io.paymentgateway.core.service.mapper.TenantWalletMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.TenantWallet}.
 */
@Service
@Transactional
public class TenantWalletServiceImpl implements TenantWalletService {

    private static final Logger LOG = LoggerFactory.getLogger(TenantWalletServiceImpl.class);

    private final TenantWalletRepository tenantWalletRepository;

    private final TenantWalletMapper tenantWalletMapper;

    public TenantWalletServiceImpl(TenantWalletRepository tenantWalletRepository, TenantWalletMapper tenantWalletMapper) {
        this.tenantWalletRepository = tenantWalletRepository;
        this.tenantWalletMapper = tenantWalletMapper;
    }

    @Override
    public TenantWalletDTO save(TenantWalletDTO tenantWalletDTO) {
        LOG.debug("Request to save TenantWallet : {}", tenantWalletDTO);
        TenantWallet tenantWallet = tenantWalletMapper.toEntity(tenantWalletDTO);
        tenantWallet = tenantWalletRepository.save(tenantWallet);
        return tenantWalletMapper.toDto(tenantWallet);
    }

    @Override
    public TenantWalletDTO update(TenantWalletDTO tenantWalletDTO) {
        LOG.debug("Request to update TenantWallet : {}", tenantWalletDTO);
        TenantWallet tenantWallet = tenantWalletMapper.toEntity(tenantWalletDTO);
        tenantWallet = tenantWalletRepository.save(tenantWallet);
        return tenantWalletMapper.toDto(tenantWallet);
    }

    @Override
    public Optional<TenantWalletDTO> partialUpdate(TenantWalletDTO tenantWalletDTO) {
        LOG.debug("Request to partially update TenantWallet : {}", tenantWalletDTO);

        return tenantWalletRepository
            .findById(tenantWalletDTO.getId())
            .map(existingTenantWallet -> {
                tenantWalletMapper.partialUpdate(existingTenantWallet, tenantWalletDTO);

                return existingTenantWallet;
            })
            .map(tenantWalletRepository::save)
            .map(tenantWalletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TenantWalletDTO> findOne(Long id) {
        LOG.debug("Request to get TenantWallet : {}", id);
        return tenantWalletRepository.findById(id).map(tenantWalletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TenantWallet : {}", id);
        tenantWalletRepository.deleteById(id);
    }
}
