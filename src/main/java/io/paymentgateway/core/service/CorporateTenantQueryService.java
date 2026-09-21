package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.repository.CorporateTenantRepository;
import io.paymentgateway.core.service.criteria.CorporateTenantCriteria;
import io.paymentgateway.core.service.dto.CorporateTenantDTO;
import io.paymentgateway.core.service.mapper.CorporateTenantMapper;
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
 * Service for executing complex queries for {@link CorporateTenant} entities in the database.
 * The main input is a {@link CorporateTenantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CorporateTenantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CorporateTenantQueryService extends QueryService<CorporateTenant> {

    private static final Logger LOG = LoggerFactory.getLogger(CorporateTenantQueryService.class);

    private final CorporateTenantRepository corporateTenantRepository;

    private final CorporateTenantMapper corporateTenantMapper;

    public CorporateTenantQueryService(CorporateTenantRepository corporateTenantRepository, CorporateTenantMapper corporateTenantMapper) {
        this.corporateTenantRepository = corporateTenantRepository;
        this.corporateTenantMapper = corporateTenantMapper;
    }

    /**
     * Return a {@link Page} of {@link CorporateTenantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CorporateTenantDTO> findByCriteria(CorporateTenantCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CorporateTenant> specification = createSpecification(criteria);
        return corporateTenantRepository.findAll(specification, page).map(corporateTenantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CorporateTenantCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CorporateTenant> specification = createSpecification(criteria);
        return corporateTenantRepository.count(specification);
    }

    /**
     * Function to convert {@link CorporateTenantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CorporateTenant> createSpecification(CorporateTenantCriteria criteria) {
        Specification<CorporateTenant> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CorporateTenant_.id),
                    buildStringSpecification(criteria.getLegalBusinessName(), CorporateTenant_.legalBusinessName),
                    buildStringSpecification(criteria.getBusinessRegistrationNumber(), CorporateTenant_.businessRegistrationNumber),
                    buildStringSpecification(criteria.getTaxIdentificationNumber(), CorporateTenant_.taxIdentificationNumber),
                    buildSpecification(criteria.getOperatingJurisdiction(), CorporateTenant_.operatingJurisdiction),
                    buildSpecification(criteria.getStatus(), CorporateTenant_.status),
                    buildSpecification(criteria.getKycStatus(), CorporateTenant_.kycStatus),
                    buildRangeSpecification(criteria.getRiskScore(), CorporateTenant_.riskScore),
                    buildRangeSpecification(criteria.getCreatedAt(), CorporateTenant_.createdAt),
                    buildRangeSpecification(criteria.getActivatedAt(), CorporateTenant_.activatedAt),
                    buildSpecification(criteria.getTenantDirectorId(), root ->
                        root.join(CorporateTenant_.tenantDirectors, JoinType.LEFT).get(TenantDirector_.id)
                    ),
                    buildSpecification(criteria.getApiKeyId(), root -> root.join(CorporateTenant_.apiKeys, JoinType.LEFT).get(ApiKey_.id)),
                    buildSpecification(criteria.getTenantDomainId(), root ->
                        root.join(CorporateTenant_.tenantDomains, JoinType.LEFT).get(TenantDomain_.id)
                    )
                )
            );
        }
        return specification;
    }
}
