package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.AuditLogEntry;
import io.paymentgateway.core.repository.AuditLogEntryRepository;
import io.paymentgateway.core.service.criteria.AuditLogEntryCriteria;
import io.paymentgateway.core.service.dto.AuditLogEntryDTO;
import io.paymentgateway.core.service.mapper.AuditLogEntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AuditLogEntry} entities in the database.
 * The main input is a {@link AuditLogEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuditLogEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuditLogEntryQueryService extends QueryService<AuditLogEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogEntryQueryService.class);

    private final AuditLogEntryRepository auditLogEntryRepository;

    private final AuditLogEntryMapper auditLogEntryMapper;

    public AuditLogEntryQueryService(AuditLogEntryRepository auditLogEntryRepository, AuditLogEntryMapper auditLogEntryMapper) {
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.auditLogEntryMapper = auditLogEntryMapper;
    }

    /**
     * Return a {@link Page} of {@link AuditLogEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogEntryDTO> findByCriteria(AuditLogEntryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuditLogEntry> specification = createSpecification(criteria);
        return auditLogEntryRepository.findAll(specification, page).map(auditLogEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuditLogEntryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuditLogEntry> specification = createSpecification(criteria);
        return auditLogEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link AuditLogEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuditLogEntry> createSpecification(AuditLogEntryCriteria criteria) {
        Specification<AuditLogEntry> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AuditLogEntry_.id),
                    buildSpecification(criteria.getActorType(), AuditLogEntry_.actorType),
                    buildStringSpecification(criteria.getActorId(), AuditLogEntry_.actorId),
                    buildStringSpecification(criteria.getAction(), AuditLogEntry_.action),
                    buildStringSpecification(criteria.getEntityType(), AuditLogEntry_.entityType),
                    buildStringSpecification(criteria.getEntityId(), AuditLogEntry_.entityId),
                    buildStringSpecification(criteria.getPreviousHash(), AuditLogEntry_.previousHash),
                    buildStringSpecification(criteria.getEntryHash(), AuditLogEntry_.entryHash),
                    buildRangeSpecification(criteria.getRecordedAt(), AuditLogEntry_.recordedAt)
                )
            );
        }
        return specification;
    }
}
