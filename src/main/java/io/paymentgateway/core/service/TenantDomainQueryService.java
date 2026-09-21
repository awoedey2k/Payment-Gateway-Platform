package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.TenantDomain;
import io.paymentgateway.core.repository.TenantDomainRepository;
import io.paymentgateway.core.service.criteria.TenantDomainCriteria;
import io.paymentgateway.core.service.dto.TenantDomainDTO;
import io.paymentgateway.core.service.mapper.TenantDomainMapper;
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
 * Service for executing complex queries for {@link TenantDomain} entities in the database.
 * The main input is a {@link TenantDomainCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TenantDomainDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TenantDomainQueryService extends QueryService<TenantDomain> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDomainQueryService.class);

    private final TenantDomainRepository tenantDomainRepository;

    private final TenantDomainMapper tenantDomainMapper;

    public TenantDomainQueryService(TenantDomainRepository tenantDomainRepository, TenantDomainMapper tenantDomainMapper) {
        this.tenantDomainRepository = tenantDomainRepository;
        this.tenantDomainMapper = tenantDomainMapper;
    }

    /**
     * Return a {@link Page} of {@link TenantDomainDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDomainDTO> findByCriteria(TenantDomainCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TenantDomain> specification = createSpecification(criteria);
        return tenantDomainRepository.findAll(specification, page).map(tenantDomainMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantDomainCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TenantDomain> specification = createSpecification(criteria);
        return tenantDomainRepository.count(specification);
    }

    /**
     * Function to convert {@link TenantDomainCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TenantDomain> createSpecification(TenantDomainCriteria criteria) {
        Specification<TenantDomain> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TenantDomain_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TenantDomain_.id),
                    buildStringSpecification(criteria.getCustomDomain(), TenantDomain_.customDomain),
                    buildStringSpecification(criteria.getSupportedLocales(), TenantDomain_.supportedLocales),
                    buildSpecification(criteria.getIsVerified(), TenantDomain_.isVerified),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(TenantDomain_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
