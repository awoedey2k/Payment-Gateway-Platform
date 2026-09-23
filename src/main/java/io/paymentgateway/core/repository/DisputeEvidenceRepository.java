package io.paymentgateway.core.repository;

import io.paymentgateway.core.domain.DisputeEvidence;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DisputeEvidence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, Long>, JpaSpecificationExecutor<DisputeEvidence> {}
