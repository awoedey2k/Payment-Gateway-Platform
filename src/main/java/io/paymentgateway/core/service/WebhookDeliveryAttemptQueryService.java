package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.WebhookDeliveryAttempt;
import io.paymentgateway.core.repository.WebhookDeliveryAttemptRepository;
import io.paymentgateway.core.service.criteria.WebhookDeliveryAttemptCriteria;
import io.paymentgateway.core.service.dto.WebhookDeliveryAttemptDTO;
import io.paymentgateway.core.service.mapper.WebhookDeliveryAttemptMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link WebhookDeliveryAttempt} entities in the database.
 * The main input is a {@link WebhookDeliveryAttemptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WebhookDeliveryAttemptDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WebhookDeliveryAttemptQueryService extends QueryService<WebhookDeliveryAttempt> {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookDeliveryAttemptQueryService.class);

    private final WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository;

    private final WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper;

    public WebhookDeliveryAttemptQueryService(
        WebhookDeliveryAttemptRepository webhookDeliveryAttemptRepository,
        WebhookDeliveryAttemptMapper webhookDeliveryAttemptMapper
    ) {
        this.webhookDeliveryAttemptRepository = webhookDeliveryAttemptRepository;
        this.webhookDeliveryAttemptMapper = webhookDeliveryAttemptMapper;
    }

    /**
     * Return a {@link Page} of {@link WebhookDeliveryAttemptDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookDeliveryAttemptDTO> findByCriteria(WebhookDeliveryAttemptCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WebhookDeliveryAttempt> specification = createSpecification(criteria);
        return webhookDeliveryAttemptRepository.findAll(specification, page).map(webhookDeliveryAttemptMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WebhookDeliveryAttemptCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WebhookDeliveryAttempt> specification = createSpecification(criteria);
        return webhookDeliveryAttemptRepository.count(specification);
    }

    /**
     * Function to convert {@link WebhookDeliveryAttemptCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WebhookDeliveryAttempt> createSpecification(WebhookDeliveryAttemptCriteria criteria) {
        Specification<WebhookDeliveryAttempt> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(WebhookDeliveryAttempt_.subscription, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), WebhookDeliveryAttempt_.id),
                    buildStringSpecification(criteria.getEventType(), WebhookDeliveryAttempt_.eventType),
                    buildSpecification(criteria.getStatus(), WebhookDeliveryAttempt_.status),
                    buildRangeSpecification(criteria.getHttpStatusCode(), WebhookDeliveryAttempt_.httpStatusCode),
                    buildRangeSpecification(criteria.getAttemptNumber(), WebhookDeliveryAttempt_.attemptNumber),
                    buildRangeSpecification(criteria.getAttemptedAt(), WebhookDeliveryAttempt_.attemptedAt),
                    buildSpecification(criteria.getSubscriptionId(), root ->
                        root.join(WebhookDeliveryAttempt_.subscription, JoinType.LEFT).get(WebhookSubscription_.id)
                    )
                )
            );
        }
        return specification;
    }
}
