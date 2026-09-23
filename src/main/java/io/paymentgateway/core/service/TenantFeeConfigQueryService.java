package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.TenantFeeConfig;
import io.paymentgateway.core.repository.TenantFeeConfigRepository;
import io.paymentgateway.core.service.criteria.TenantFeeConfigCriteria;
import io.paymentgateway.core.service.dto.TenantFeeConfigDTO;
import io.paymentgateway.core.service.mapper.TenantFeeConfigMapper;
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
 * Service for executing complex queries for {@link TenantFeeConfig} entities in the database.
 * The main input is a {@link TenantFeeConfigCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TenantFeeConfigDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TenantFeeConfigQueryService extends QueryService<TenantFeeConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantFeeConfigQueryService.class);

    private final TenantFeeConfigRepository tenantFeeConfigRepository;

    private final TenantFeeConfigMapper tenantFeeConfigMapper;

    public TenantFeeConfigQueryService(TenantFeeConfigRepository tenantFeeConfigRepository, TenantFeeConfigMapper tenantFeeConfigMapper) {
        this.tenantFeeConfigRepository = tenantFeeConfigRepository;
        this.tenantFeeConfigMapper = tenantFeeConfigMapper;
    }

    /**
     * Return a {@link Page} of {@link TenantFeeConfigDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantFeeConfigDTO> findByCriteria(TenantFeeConfigCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TenantFeeConfig> specification = createSpecification(criteria);
        return tenantFeeConfigRepository.findAll(specification, page).map(tenantFeeConfigMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantFeeConfigCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TenantFeeConfig> specification = createSpecification(criteria);
        return tenantFeeConfigRepository.count(specification);
    }

    /**
     * Function to convert {@link TenantFeeConfigCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TenantFeeConfig> createSpecification(TenantFeeConfigCriteria criteria) {
        Specification<TenantFeeConfig> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TenantFeeConfig_.tenant, JoinType.LEFT);
                root.fetch(TenantFeeConfig_.countryPaymentMethod, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TenantFeeConfig_.id),
                    buildRangeSpecification(criteria.getFixedFee(), TenantFeeConfig_.fixedFee),
                    buildRangeSpecification(criteria.getPercentageFee(), TenantFeeConfig_.percentageFee),
                    buildRangeSpecification(criteria.getCapAmount(), TenantFeeConfig_.capAmount),
                    buildSpecification(criteria.getFeeBearer(), TenantFeeConfig_.feeBearer),
                    buildSpecification(criteria.getIsActive(), TenantFeeConfig_.isActive),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(TenantFeeConfig_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    ),
                    buildSpecification(criteria.getCountryPaymentMethodId(), root ->
                        root.join(TenantFeeConfig_.countryPaymentMethod, JoinType.LEFT).get(CountryPaymentMethod_.id)
                    )
                )
            );
        }
        return specification;
    }
}
