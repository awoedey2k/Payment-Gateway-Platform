package io.paymentgateway.core.extended.tenant.service.onboarding;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.domain.KycAssessment;
import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import io.paymentgateway.core.extended.tenant.repository.ExtendedCorporateTenantRepository;
import io.paymentgateway.core.extended.tenant.repository.ExtendedTenantDirectorRepository;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.service.kyc.KycScoringPolicy;
import io.paymentgateway.core.extended.tenant.service.kyc.KycSignalCollector;
import io.paymentgateway.core.extended.tenant.service.kyc.OnboardingSnapshot;
import io.paymentgateway.core.extended.tenant.service.kyc.PartyToScreen;
import io.paymentgateway.core.extended.tenant.service.lifecycle.TenantLifecycleService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * KYB/KYC onboarding workflow (spec Chunk 2 §2, Figure 2.2).
 *
 * <pre>
 * screen():  NOT_STARTED -> PENDING -> (score 0-20)  APPROVED       + tenant ACTIVE
 *                                    -> (score 21-60) MANUAL_REVIEW  + tenant stays PENDING_REVIEW
 *                                    -> (score 61-100) REJECTED      + tenant REJECTED
 * approveManually() / rejectManually(): MANUAL_REVIEW -> APPROVED (+ACTIVE) | REJECTED (+REJECTED)
 * </pre>
 *
 * The provider call-outs run <em>between</em> two short transactions, so no database transaction or tenant lock is held
 * while waiting on a third party. The PENDING marker written by the first transaction makes a concurrent second screening
 * fail fast instead of double-processing.
 *
 * <p>LIVE API keys are not created here: the merchant's secret can only be shown once, to whoever requests it, so keys are
 * issued on request once the tenant is ACTIVE (see {@code ApiKeyService.issueKey}).
 */
@Service
public class OnboardingService {

    private static final Logger LOG = LoggerFactory.getLogger(OnboardingService.class);

    private final ExtendedCorporateTenantRepository tenants;
    private final ExtendedTenantDirectorRepository directors;
    private final KycSignalCollector collector;
    private final KycScoringPolicy scoring;
    private final TenantLifecycleService lifecycle;
    private final TransactionTemplate tx;

    public OnboardingService(
        ExtendedCorporateTenantRepository tenants,
        ExtendedTenantDirectorRepository directors,
        KycSignalCollector collector,
        KycScoringPolicy scoring,
        TenantLifecycleService lifecycle,
        TransactionTemplate tx
    ) {
        this.tenants = tenants;
        this.directors = directors;
        this.collector = collector;
        this.scoring = scoring;
        this.lifecycle = lifecycle;
        this.tx = tx;
    }

    /** Runs sanctions/PEP screening and the registry cross-reference, scores the result and applies the outcome. */
    public KycAssessment screen(Long tenantId) {
        OnboardingSnapshot snapshot = tx.execute(status -> begin(tenantId));
        try {
            Set<KycSignal> signals = collector.collect(snapshot);
            KycAssessment assessment = scoring.assess(signals);
            tx.executeWithoutResult(status -> complete(tenantId, assessment));
            return assessment;
        } catch (RuntimeException e) {
            // Do not leave the tenant stuck in PENDING if something unexpected failed after the marker was written.
            tx.executeWithoutResult(status -> resetIfPending(tenantId));
            throw e;
        }
    }

    /** Compliance approval of an application held for manual review. */
    public CorporateTenant approveManually(Long tenantId, String reason) {
        return tx.execute(status -> {
            CorporateTenant tenant = lockManualReview(tenantId);
            tenant.setKycStatus(KycStatus.APPROVED);
            lifecycle.applyTransition(tenant, TenantStatus.ACTIVE, "KYC manually approved: " + reason);
            return tenant;
        });
    }

    /** Compliance rejection of an application held for manual review. */
    public CorporateTenant rejectManually(Long tenantId, String reason) {
        return tx.execute(status -> {
            CorporateTenant tenant = lockManualReview(tenantId);
            lifecycle.applyTransition(tenant, TenantStatus.REJECTED, "KYC manually rejected: " + reason);
            return tenant;
        });
    }

    private OnboardingSnapshot begin(Long tenantId) {
        CorporateTenant tenant = lock(tenantId);
        if (tenant.getStatus() != TenantStatus.PENDING_REVIEW) {
            throw TenantOperationException.conflict(
                "TENANT_NOT_PENDING_REVIEW",
                "Tenant " + tenantId + " is " + tenant.getStatus() + "; only PENDING_REVIEW tenants are screened"
            );
        }
        if (tenant.getKycStatus() == KycStatus.PENDING) {
            throw TenantOperationException.conflict("KYC_IN_PROGRESS", "A KYC screening is already running for tenant " + tenantId);
        }
        if (tenant.getKycStatus() != KycStatus.NOT_STARTED) {
            throw TenantOperationException.conflict("KYC_ALREADY_DECIDED", "Tenant " + tenantId + " kycStatus is " + tenant.getKycStatus());
        }
        tenant.setKycStatus(KycStatus.PENDING);
        tenants.save(tenant);
        var parties = directors
            .findByTenantId(tenantId)
            .stream()
            .map(d -> new PartyToScreen(d.getFullName(), d.getDateOfBirth(), d.getNationality()))
            .toList();
        return new OnboardingSnapshot(
            tenantId,
            tenant.getLegalBusinessName(),
            tenant.getBusinessRegistrationNumber(),
            tenant.getOperatingJurisdiction(),
            parties
        );
    }

    private void complete(Long tenantId, KycAssessment assessment) {
        CorporateTenant tenant = lock(tenantId);
        if (tenant.getKycStatus() != KycStatus.PENDING) {
            throw TenantOperationException.conflict(
                "KYC_STATE_CHANGED",
                "Tenant " + tenantId + " kycStatus changed during screening to " + tenant.getKycStatus()
            );
        }
        tenant.setRiskScore(assessment.score());
        switch (assessment.decision()) {
            case APPROVE -> {
                tenant.setKycStatus(KycStatus.APPROVED);
                lifecycle.applyTransition(tenant, TenantStatus.ACTIVE, "KYC auto-approved, score " + assessment.score());
            }
            case MANUAL_REVIEW -> {
                tenant.setKycStatus(KycStatus.MANUAL_REVIEW);
                tenants.save(tenant);
            }
            case REJECT -> lifecycle.applyTransition(tenant, TenantStatus.REJECTED, "KYC auto-rejected, score " + assessment.score());
        }
        LOG.info(
            "KYC decision for tenant {}: {} (score {}, signals {})",
            tenantId,
            assessment.decision(),
            assessment.score(),
            assessment.signals()
        );
    }

    private void resetIfPending(Long tenantId) {
        tenants
            .findByIdForUpdate(tenantId)
            .filter(t -> t.getKycStatus() == KycStatus.PENDING)
            .ifPresent(t -> {
                t.setKycStatus(KycStatus.NOT_STARTED);
                tenants.save(t);
            });
    }

    private CorporateTenant lockManualReview(Long tenantId) {
        CorporateTenant tenant = lock(tenantId);
        if (tenant.getStatus() != TenantStatus.PENDING_REVIEW || tenant.getKycStatus() != KycStatus.MANUAL_REVIEW) {
            throw TenantOperationException.conflict(
                "NOT_IN_MANUAL_REVIEW",
                "Tenant " +
                    tenantId +
                    " is " +
                    tenant.getStatus() +
                    " with kycStatus " +
                    tenant.getKycStatus() +
                    "; expected PENDING_REVIEW / MANUAL_REVIEW"
            );
        }
        return tenant;
    }

    private CorporateTenant lock(Long tenantId) {
        return tenants.findByIdForUpdate(tenantId).orElseThrow(() -> TenantOperationException.notFound("Tenant", tenantId));
    }
}
