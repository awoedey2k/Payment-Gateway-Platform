package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.WebhookSubscription;
import io.paymentgateway.core.repository.WebhookSubscriptionRepository;
import io.paymentgateway.core.service.criteria.WebhookSubscriptionCriteria;
import io.paymentgateway.core.service.dto.WebhookSubscriptionDTO;
import io.paymentgateway.core.service.mapper.WebhookSubscriptionMapper;
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
 * Service for executing complex queries for {@link WebhookSubscription} entities in the database.
 * The main input is a {@link WebhookSubscriptionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link WebhookSubscriptionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WebhookSubscriptionQueryService extends QueryService<WebhookSubscription> {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookSubscriptionQueryService.class);

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    private final WebhookSubscriptionMapper webhookSubscriptionMapper;

    public WebhookSubscriptionQueryService(
        WebhookSubscriptionRepository webhookSubscriptionRepository,
        WebhookSubscriptionMapper webhookSubscriptionMapper
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.webhookSubscriptionMapper = webhookSubscriptionMapper;
    }

    /**
     * Return a {@link Page} of {@link WebhookSubscriptionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WebhookSubscriptionDTO> findByCriteria(WebhookSubscriptionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WebhookSubscription> specification = createSpecification(criteria);
        return webhookSubscriptionRepository.findAll(specification, page).map(webhookSubscriptionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WebhookSubscriptionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<WebhookSubscription> specification = createSpecification(criteria);
        return webhookSubscriptionRepository.count(specification);
    }

    /**
     * Function to convert {@link WebhookSubscriptionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WebhookSubscription> createSpecification(WebhookSubscriptionCriteria criteria) {
        Specification<WebhookSubscription> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(WebhookSubscription_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), WebhookSubscription_.id),
                    buildStringSpecification(criteria.getTargetUrl(), WebhookSubscription_.targetUrl),
                    buildStringSpecification(criteria.getSecretHash(), WebhookSubscription_.secretHash),
                    buildSpecification(criteria.getIsActive(), WebhookSubscription_.isActive),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(WebhookSubscription_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
