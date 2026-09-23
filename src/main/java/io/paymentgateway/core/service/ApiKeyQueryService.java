package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.repository.ApiKeyRepository;
import io.paymentgateway.core.service.criteria.ApiKeyCriteria;
import io.paymentgateway.core.service.dto.ApiKeyDTO;
import io.paymentgateway.core.service.mapper.ApiKeyMapper;
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
 * Service for executing complex queries for {@link ApiKey} entities in the database.
 * The main input is a {@link ApiKeyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ApiKeyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ApiKeyQueryService extends QueryService<ApiKey> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyQueryService.class);

    private final ApiKeyRepository apiKeyRepository;

    private final ApiKeyMapper apiKeyMapper;

    public ApiKeyQueryService(ApiKeyRepository apiKeyRepository, ApiKeyMapper apiKeyMapper) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyMapper = apiKeyMapper;
    }

    /**
     * Return a {@link Page} of {@link ApiKeyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ApiKeyDTO> findByCriteria(ApiKeyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ApiKey> specification = createSpecification(criteria);
        return apiKeyRepository.findAll(specification, page).map(apiKeyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ApiKeyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ApiKey> specification = createSpecification(criteria);
        return apiKeyRepository.count(specification);
    }

    /**
     * Function to convert {@link ApiKeyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ApiKey> createSpecification(ApiKeyCriteria criteria) {
        Specification<ApiKey> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(ApiKey_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), ApiKey_.id),
                    buildStringSpecification(criteria.getKeyPrefix(), ApiKey_.keyPrefix),
                    buildStringSpecification(criteria.getKeyHash(), ApiKey_.keyHash),
                    buildSpecification(criteria.getEnvironment(), ApiKey_.environment),
                    buildSpecification(criteria.getIsActive(), ApiKey_.isActive),
                    buildRangeSpecification(criteria.getIssuedAt(), ApiKey_.issuedAt),
                    buildRangeSpecification(criteria.getRevokedAt(), ApiKey_.revokedAt),
                    buildRangeSpecification(criteria.getGraceExpiresAt(), ApiKey_.graceExpiresAt),
                    buildSpecification(criteria.getTenantId(), root -> root.join(ApiKey_.tenant, JoinType.LEFT).get(CorporateTenant_.id))
                )
            );
        }
        return specification;
    }
}
