package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.WebhookDeliveryAttempt;
import io.paymentgateway.core.repository.WebhookDeliveryAttemptRepository;
import io.paymentgateway.core.service.WebhookDeliveryAttemptService;
import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
import io.paymentgateway.core.service.mapper.WebhookDeliveryAttemptMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.WebhookDeliveryAttempt}.
 */
@Service
@Transactional
public class WebhookDeliveryAttemptServiceImpl implements WebhookDeliveryAttemptService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryAttemptServiceImpl.class);

    private final WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository;

    private final WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper;

    public WebhookDeliveryAttemptServiceImpl(
        WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository,
        WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper
    ) {
        this.webhookDeliveryAttemptRepository = webhookDeliveryAttemptRepository;
        this.webhookDeliveryAttemptMapper = webhookDeliveryAttemptMapper;
    }

    @Override
    public WebhookDeliveryAttemptDTO save(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO) {
        LOG.debug("Request to save WebhookDeliveryAttempt : {}", webhookDeliveryAttemptDTO);
        WebhookDeliveryAttempt webhookDeliveryAttempt = webhookDeliveryAttemptMapper.toEntity(webhookDeliveryAttemptDTO);
        webhookDeliveryAttempt = webhookDeliveryAttemptRepository.save(webhookDeliveryAttempt);
        return webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);
    }

    @Override
    public WebhookDeliveryAttemptDTO update(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO) {
        LOG.debug("Request to update WebhookDeliveryAttempt : {}", webhookDeliveryAttemptDTO);
        WebhookDeliveryAttempt webhookDeliveryAttempt = webhookDeliveryAttemptMapper.toEntity(webhookDeliveryAttemptDTO);
        webhookDeliveryAttempt = webhookDeliveryAttemptRepository.save(webhookDeliveryAttempt);
        return webhookDeliveryAttemptMapper.toDto(webhookDeliveryAttempt);
    }

    @Override
    public Optional<WebhookDeliveryAttemptDTO> partialUpdate(WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO) {
        LOG.debug("Request to partially update WebhookDeliveryAttempt : {}", webhookDeliveryAttemptDTO);

        return webhookDeliveryAttemptRepository
            .findById(webhookDeliveryAttemptDTO.getId())
            .map(existingWebhookDeliveryAttempt -> {
                webhookDeliveryAttemptMapper.partialUpdate(existingWebhookDeliveryAttempt, webhookDeliveryAttemptDTO);

                return existingWebhookDeliveryAttempt;
            })
            .map(webhookDeliveryAttemptRepository::save)
            .map(webhookDeliveryAttemptMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebhookDeliveryAttemptDTO> findOne(Long id) {
        LOG.debug("Request to get WebhookDeliveryAttempt : {}", id);
        return webhookDeliveryAttemptRepository.findById(id).map(webhookDeliveryAttemptMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WebhookDeliveryAttempt : {}", id);
        webhookDeliveryAttemptRepository.deleteById(id);
    }
}
