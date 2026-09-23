package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.TenantWallet;
import io.paymentgateway.core.repository.TenantWalletRepository;
import io.paymentgateway.core.service.criteria.TenantWalletCriteria;
import io.paymentgateway.core.service.dto.TenantWalletDTO;
import io.paymentgateway.core.service.mapper.TenantWalletMapper;
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
 * Service for executing complex queries for {@link TenantWallet} entities in the database.
 * The main input is a {@link TenantWalletCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TenantWalletDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TenantWalletQueryService extends QueryService<TenantWallet> {

    private static final Logger LOG = LoggerFactory.getLogger(TenantWalletQueryService.class);

    private final TenantWalletRepository tenantWalletRepository;

    private final TenantWalletMapper tenantWalletMapper;

    public TenantWalletQueryService(TenantWalletRepository tenantWalletRepository, TenantWalletMapper tenantWalletMapper) {
        this.tenantWalletRepository = tenantWalletRepository;
        this.tenantWalletMapper = tenantWalletMapper;
    }

    /**
     * Return a {@link Page} of {@link TenantWalletDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TenantWalletDTO> findByCriteria(TenantWalletCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TenantWallet> specification = createSpecification(criteria);
        return tenantWalletRepository.findAll(specification, page).map(tenantWalletMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantWalletCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TenantWallet> specification = createSpecification(criteria);
        return tenantWalletRepository.count(specification);
    }

    /**
     * Function to convert {@link TenantWalletCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TenantWallet> createSpecification(TenantWalletCriteria criteria) {
        Specification<TenantWallet> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(TenantWallet_.tenant, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TenantWallet_.id),
                    buildStringSpecification(criteria.getCurrencyCode(), TenantWallet_.currencyCode),
                    buildRangeSpecification(criteria.getAvailableBalance(), TenantWallet_.availableBalance),
                    buildRangeSpecification(criteria.getLockedBalance(), TenantWallet_.lockedBalance),
                    buildSpecification(criteria.getTenantId(), root ->
                        root.join(TenantWallet_.tenant, JoinType.LEFT).get(CorporateTenant_.id)
                    )
                )
            );
        }
        return specification;
    }
}
