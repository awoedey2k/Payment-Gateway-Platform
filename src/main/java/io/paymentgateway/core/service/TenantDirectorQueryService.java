package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.TenantDirector;
import io.paymentgateway.core.repository.TenantDirectorRepository;
import io.paymentgateway.core.service.criteria.TenantDirectorCriteria;
import io.paymentgateway.core.service.dto.TenantDirectorDTO;
import io.paymentgateway.core.service.mapper.TenantDirectorMapper;
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
 * Service for executing complex queries for {@link TenantDirector} entities in the database.
 * The main input is a {@link TenantDirectorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TenantDirectorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TenantDirectorQueryService extends QueryService<TenantDirector> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDirectorQueryService.class);

    private final TenantDirectorRepository tenantDirectorRepository;

    private final TenantDirectorMapper tenantDirectorMapper;

    public TenantDirectorQueryService(TenantDirectorRepository tenantDirectorRepository, TenantDirectorMapper tenantDirectorMapper) {
        this.tenantDirectorRepository = tenantDirectorRepository;
        this.tenantDirectorMapper = tenantDirectorMapper;
    }

    /**
     * Return a {@link Page} of {@link TenantDirectorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantDirectorDTO> findByCriteria(TenantDirectorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TenantDirector> specification = createSpecification(criteria);
        return tenantDirectorRepository.findAll(specification, page).map(tenantDirectorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantDirectorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TenantDirector> specification = createSpecification(criteria);
        return tenantDirectorRepository.count(specification);
    }

    /**
     * Function to convert {@link TenantDirectorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TenantDirector> createSpecification(TenantDirectorCriteria criteria) {
        Specification<TenantDirector> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TenantDirector_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TenantDirector_.id),
                    buildStringSpecification(criteria.getFullName(), TenantDirector_.fullName),
                    buildRangeSpecification(criteria.getDateOfBirth(), TenantDirector_.dateOfBirth),
                    buildSpecification(criteria.getNationality(), TenantDirector_.nationality),
                    buildSpecification(criteria.getIdentificationType(), TenantDirector_.identificationType),
                    buildStringSpecification(criteria.getIdentificationNumber(), TenantDirector_.identificationNumber),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(TenantDirector_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
