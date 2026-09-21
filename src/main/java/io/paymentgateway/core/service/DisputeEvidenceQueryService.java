package io.paymentgateway.core.service;

import io.paymentgateway.core.domain.*; // for static metamodels
import io.paymentgateway.core.domain.DisputeEvidence;
import io.paymentgateway.core.repository.DisputeEvidenceRepository;
import io.paymentgateway.core.service.criteria.DisputeEvidenceCriteria;
import io.paymentgateway.core.service.dto.DisputeEvidenceDTO;
import io.paymentgateway.core.service.mapper.DisputeEvidenceMapper;
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
 * Service for executing complex queries for {@link DisputeEvidence} entities in the database.
 * The main input is a {@link DisputeEvidenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DisputeEvidenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DisputeEvidenceQueryService extends QueryService<DisputeEvidence> {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeEvidenceQueryService.class);

    private final DisputeEvidenceRepository disputeEvidenceRepository;

    private final DisputeEvidenceMapper disputeEvidenceMapper;

    public DisputeEvidenceQueryService(DisputeEvidenceRepository disputeEvidenceRepository, DisputeEvidenceMapper disputeEvidenceMapper) {
        this.disputeEvidenceRepository = disputeEvidenceRepository;
        this.disputeEvidenceMapper = disputeEvidenceMapper;
    }

    /**
     * Return a {@link Page} of {@link DisputeEvidenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DisputeEvidenceDTO> findByCriteria(DisputeEvidenceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DisputeEvidence> specification = createSpecification(criteria);
        return disputeEvidenceRepository.findAll(specification, page).map(disputeEvidenceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DisputeEvidenceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DisputeEvidence> specification = createSpecification(criteria);
        return disputeEvidenceRepository.count(specification);
    }

    /**
     * Function to convert {@link DisputeEvidenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DisputeEvidence> createSpecification(DisputeEvidenceCriteria criteria) {
        Specification<DisputeEvidence> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(DisputeEvidence_.dispute, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DisputeEvidence_.id),
                    buildSpecification(criteria.getEvidenceType(), DisputeEvidence_.evidenceType),
                    buildStringSpecification(criteria.getFileName(), DisputeEvidence_.fileName),
                    buildStringSpecification(criteria.getFileUrl(), DisputeEvidence_.fileUrl),
                    buildRangeSpecification(criteria.getFileSizeBytes(), DisputeEvidence_.fileSizeBytes),
                    buildStringSpecification(criteria.getMimeType(), DisputeEvidence_.mimeType),
                    buildStringSpecification(criteria.getSha256Checksum(), DisputeEvidence_.sha256Checksum),
                    buildRangeSpecification(criteria.getUploadedAt(), DisputeEvidence_.uploadedAt),
                    buildSpecification(criteria.getDisputeId(), root -> root.join(DisputeEvidence_.dispute, JoinType.LEFT).get(Dispute_.id))
                )
            );
        }
        return specification;
    }
}
