package io.paymentgateway.core.service.impl;

import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.repository.WebhookSubscriptionRepository;
import io.paymentgateway.core.service.WebhookSubscriptionService;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import io.paymentgateway.core.service.mapper.WebhookSubscriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.paymentgateway.core.domain.WebhookSubscription}.
 */
@Service
@Transactional
public class WebhookSubscriptionServiceImpl implements WebhookSubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookSubscriptionServiceImpl.class);

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    private final WebhookSubscriptionMapper webhookSubscriptionMapper;

    public WebhookSubscriptionServiceImpl(
        WebhookSubscriptionRepository webhookSubscriptionRepository,
        WebhookSubscriptionMapper webhookSubscriptionMapper
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.webhookSubscriptionMapper = webhookSubscriptionMapper;
    }

    @Override
    public WebhookSubscriptionDTO save(WebhookSubscriptionDTO webhookSubscriptionDTO) {
        LOG.debug("Request to save WebhookSubscription : {}", webhookSubscriptionDTO);
        WebhookSubscription webhookSubscription = webhookSubscriptionMapper.toEntity(webhookSubscriptionDTO);
        webhookSubscription = webhookSubscriptionRepository.save(webhookSubscription);
        return webhookSubscriptionMapper.toDto(webhookSubscription);
    }

    @Override
    public WebhookSubscriptionDTO update(WebhookSubscriptionDTO webhookSubscriptionDTO) {
        LOG.debug("Request to update WebhookSubscription : {}", webhookSubscriptionDTO);
        WebhookSubscription webhookSubscription = webhookSubscriptionMapper.toEntity(webhookSubscriptionDTO);
        webhookSubscription = webhookSubscriptionRepository.save(webhookSubscription);
        return webhookSubscriptionMapper.toDto(webhookSubscription);
    }

    @Override
    public Optional<WebhookSubscriptionDTO> partialUpdate(WebhookSubscriptionDTO webhookSubscriptionDTO) {
        LOG.debug("Request to partially update WebhookSubscription : {}", webhookSubscriptionDTO);

        return webhookSubscriptionRepository
            .findById(webhookSubscriptionDTO.getId())
            .map(existingWebhookSubscription -> {
                webhookSubscriptionMapper.partialUpdate(existingWebhookSubscription, webhookSubscriptionDTO);

                return existingWebhookSubscription;
            })
            .map(webhookSubscriptionRepository::save)
            .map(webhookSubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebhookSubscriptionDTO> findOne(Long id) {
        LOG.debug("Request to get WebhookSubscription : {}", id);
        return webhookSubscriptionRepository.findById(id).map(webhookSubscriptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WebhookSubscription : {}", id);
        webhookSubscriptionRepository.deleteById(id);
    }
}
