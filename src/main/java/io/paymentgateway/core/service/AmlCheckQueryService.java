package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.AmlCheck;
import io.paymentgateway.core.repository.AmlCheckRepository;
import io.paymentgateway.core.service.criteria.AmlCheckCriteria;
import io.paymentgateway.core.service.dto.AmlCheckDTO;
import io.paymentgateway.core.service.mapper.AmlCheckMapper;
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
 * Service for executing complex queries for {@link AmlCheck} entities in the database.
 * The main input is a {@link AmlCheckCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AmlCheckDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AmlCheckQueryService extends QueryService<AmlCheck> {

    private static final Logger LOG = LoggerFactory.getLogger(AmlCheckQueryService.class);

    private final AmlCheckRepository amlCheckRepository;

    private final AmlCheckMapper amlCheckMapper;

    public AmlCheckQueryService(AmlCheckRepository amlCheckRepository, AmlCheckMapper amlCheckMapper) {
        this.amlCheckRepository = amlCheckRepository;
        this.amlCheckMapper = amlCheckMapper;
    }

    /**
     * Return a {@link Page} of {@link AmlCheckDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AmlCheckDTO> findByCriteria(AmlCheckCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AmlCheck> specification = createSpecification(criteria);
        return amlCheckRepository.findAll(specification, page).map(amlCheckMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AmlCheckCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AmlCheck> specification = createSpecification(criteria);
        return amlCheckRepository.count(specification);
    }

    /**
     * Function to convert {@link AmlCheckCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AmlCheck> createSpecification(AmlCheckCriteria criteria) {
        Specification<AmlCheck> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AmlCheck_.tenant, JoinType.LEFT);
                root.fetch(AmlCheck_.transaction, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AmlCheck_.id),
                    buildRangeSpecification(criteria.getRiskScore(), AmlCheck_.riskScore),
                    buildSpecification(criteria.getDecision(), AmlCheck_.decision),
                    buildStringSpecification(criteria.getRuleTriggered(), AmlCheck_.ruleTriggered),
                    buildRangeSpecification(criteria.getCheckedAt(), AmlCheck_.checkedAt),
                    buildSpecification(criteria.getTenantId(), root -> root.join(AmlCheck_.tenant, JoinType.LEFT).get(CorporateTenant_.id)),
                    buildSpecification(criteria.getTransactionId(), root ->
                        root.join(AmlCheck_.transaction, JoinType.LEFT).get(Transaction_.id)
                    )
                )
            );
        }
        return specification;
    }
}
