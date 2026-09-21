package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.repository.CorporateTenantRepository;
import io.paymentgateway.core.service.CorporateTenantService;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.mapper.CorporateTenantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.CorporateTenant}.
 */
@Service
@Transactional
public class CorporateTenantServiceImpl implements CorporateTenantService {

    private static final Logger LOG = LoggerFactory.getLogger(CorporateTenantServiceImpl.class);

    private final CorporateTenantRepository corporateTenantRepository;

    private final CorporateTenantMapper corporateTenantMapper;

    public CorporateTenantServiceImpl(CorporateTenantRepository corporateTenantRepository, CorporateTenantMapper corporateTenantMapper) {
        this.corporateTenantRepository = corporateTenantRepository;
        this.corporateTenantMapper = corporateTenantMapper;
    }

    @Override
    public CorporateTenantDTO save(CorporateTenantDTO corporateTenantDTO) {
        LOG.debug("Request to save CorporateTenant : {}", corporateTenantDTO);
        CorporateTenant corporateTenant = corporateTenantMapper.toEntity(corporateTenantDTO);
        corporateTenant = corporateTenantRepository.save(corporateTenant);
        return corporateTenantMapper.toDto(corporateTenant);
    }

    @Override
    public CorporateTenantDTO update(CorporateTenantDTO corporateTenantDTO) {
        LOG.debug("Request to update CorporateTenant : {}", corporateTenantDTO);
        CorporateTenant corporateTenant = corporateTenantMapper.toEntity(corporateTenantDTO);
        corporateTenant = corporateTenantRepository.save(corporateTenant);
        return corporateTenantMapper.toDto(corporateTenant);
    }

    @Override
    public Optional<CorporateTenantDTO> partialUpdate(CorporateTenantDTO corporateTenantDTO) {
        LOG.debug("Request to partially update CorporateTenant : {}", corporateTenantDTO);

        return corporateTenantRepository
            .findById(corporateTenantDTO.getId())
            .map(existingCorporateTenant -> {
                corporateTenantMapper.partialUpdate(existingCorporateTenant, corporateTenantDTO);

                return existingCorporateTenant;
            })
            .map(corporateTenantRepository::save)
            .map(corporateTenantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CorporateTenantDTO> findOne(Long id) {
        LOG.debug("Request to get CorporateTenant : {}", id);
        return corporateTenantRepository.findById(id).map(corporateTenantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CorporateTenant : {}", id);
        corporateTenantRepository.deleteById(id);
    }
}
