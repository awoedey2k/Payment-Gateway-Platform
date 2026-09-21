package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.repository.ApiKeyRepository;
import io.paymentgateway.core.service.ApiKeyService;
import io.paymentgateway.core.service.dto.ApiKeyDTO;
import io.paymentgateway.core.service.mapper.ApiKeyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.ApiKey}.
 */
@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyMapper apiKeyMapper;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository, ApiKeyMapper apiKeyMapper) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
    }

    @Override
    public ApiKeyDTO save(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to save ApiKey : {}", apiKeyDTO);
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDto(apiKey);
    }

    @Override
    public ApiKeyDTO update(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to update ApiKey : {}", apiKeyDTO);
        ApiKey apiKey = apiKeyMapper.toEntity(apiKeyDTO);
        apiKey = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDto(apiKey);
    }

    @Override
    public Optional<ApiKeyDTO> partialUpdate(ApiKeyDTO apiKeyDTO) {
        LOG.debug("Request to partially update ApiKey : {}", apiKeyDTO);

        return apiKeyRepository
            .findById(apiKeyDTO.getId())
            .map(existingApiKey -> {
                apiKeyMapper.partialUpdate(existingApiKey, apiKeyDTO);

                return existingApiKey;
            })
            .map(apiKeyRepository::save)
            .map(apiKeyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiKeyDTO> findOne(Long id) {
        LOG.debug("Request to get ApiKey : {}", id);
        return apiKeyRepository.findById(id).map(apiKeyMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApiKey : {}", id);
        apiKeyRepository.deleteById(id);
    }
}
