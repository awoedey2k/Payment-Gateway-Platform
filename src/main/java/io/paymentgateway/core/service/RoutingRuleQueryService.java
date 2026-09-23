package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.RoutingRule;
import io.paymentgateway.core.repository.RoutingRuleRepository;
import io.paymentgateway.core.service.criteria.RoutingRuleCriteria;
import io.paymentgateway.core.service.dto.RoutingRuleDTO;
import io.paymentgateway.core.service.mapper.RoutingRuleMapper;
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
 * Service for executing complex queries for {@link RoutingRule} entities in the database.
 * The main input is a {@link RoutingRuleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RoutingRuleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RoutingRuleQueryService extends QueryService<RoutingRule> {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingRuleQueryService.class);

    private final RoutingRuleRepository routingRuleRepository;

    private final RoutingRuleMapper routingRuleMapper;

    public RoutingRuleQueryService(RoutingRuleRepository routingRuleRepository, RoutingRuleMapper routingRuleMapper) {
        this.routingRuleRepository = routingRuleRepository;
        this.routingRuleMapper = routingRuleMapper;
    }

    /**
     * Return a {@link Page} of {@link RoutingRuleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RoutingRuleDTO> findByCriteria(RoutingRuleCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RoutingRule> specification = createSpecification(criteria);
        return routingRuleRepository.findAll(specification, page).map(routingRuleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RoutingRuleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RoutingRule> specification = createSpecification(criteria);
        return routingRuleRepository.count(specification);
    }

    /**
     * Function to convert {@link RoutingRuleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RoutingRule> createSpecification(RoutingRuleCriteria criteria) {
        Specification<RoutingRule> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(RoutingRule_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), RoutingRule_.id),
                    buildRangeSpecification(criteria.getPriority(), RoutingRule_.priority),
                    buildSpecification(criteria.getScope(), RoutingRule_.scope),
                    buildStringSpecification(criteria.getCountryCode(), RoutingRule_.countryCode),
                    buildStringSpecification(criteria.getCurrencyCode(), RoutingRule_.currencyCode),
                    buildStringSpecification(criteria.getCardBrand(), RoutingRule_.cardBrand),
                    buildStringSpecification(criteria.getPrimaryAdapter(), RoutingRule_.primaryAdapter),
                    buildStringSpecification(criteria.getFallbackAdapter(), RoutingRule_.fallbackAdapter),
                    buildRangeSpecification(criteria.getMaxRetries(), RoutingRule_.maxRetries),
                    buildSpecification(criteria.getIsActive(), RoutingRule_.isActive),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(RoutingRule_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
