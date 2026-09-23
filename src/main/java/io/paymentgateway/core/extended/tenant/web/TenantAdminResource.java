package io.paymentgateway.core.extended.tenant.web;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.extended.tenant.domain.IssuedApiKey;
import io.paymentgateway.core.extended.tenant.domain.KeyRotationResult;
import io.paymentgateway.core.extended.tenant.domain.KycAssessment;
import io.paymentgateway.core.extended.tenant.service.apikey.ApiKeyService;
import io.paymentgateway.core.extended.tenant.service.lifecycle.TenantLifecycleService;
import io.paymentgateway.core.extended.tenant.service.onboarding.OnboardingService;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.DecisionRequest;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.IssueKeyRequest;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.RotateKeyRequest;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.StatusChangeRequest;
import io.paymentgateway.core.extended.tenant.web.dto.TenantDtos.TenantStatusResponse;
import io.paymentgateway.core.security.AuthoritiesConstants;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Staff-facing (JWT, ROLE_ADMIN) operations on tenants: KYC screening and review, status transitions, and API key
 * management. Plain CRUD on the tenant records stays in the generated {@code /api/corporate-tenants} resource.
 *
 * <p>Key endpoints return the raw secret exactly once, in the response to issue/rotate; it cannot be retrieved afterwards.
 */
@RestController
@RequestMapping("/api/extended")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class TenantAdminResource {

    private final OnboardingService onboarding;
    private final TenantLifecycleService lifecycle;
    private final ApiKeyService apiKeys;

    public TenantAdminResource(OnboardingService onboarding, TenantLifecycleService lifecycle, ApiKeyService apiKeys) {
        this.onboarding = onboarding;
        this.lifecycle = lifecycle;
        this.apiKeys = apiKeys;
    }

    @PostMapping("/tenants/{tenantId}/onboarding/screen")
    public KycAssessment screen(@PathVariable Long tenantId) {
        return onboarding.screen(tenantId);
    }

    @PostMapping("/tenants/{tenantId}/onboarding/approve")
    public TenantStatusResponse approve(@PathVariable Long tenantId, @Valid @RequestBody DecisionRequest request) {
        return toResponse(onboarding.approveManually(tenantId, request.reason()));
    }

    @PostMapping("/tenants/{tenantId}/onboarding/reject")
    public TenantStatusResponse reject(@PathVariable Long tenantId, @Valid @RequestBody DecisionRequest request) {
        return toResponse(onboarding.rejectManually(tenantId, request.reason()));
    }

    /** Recovery for a screening that never finished; see {@link OnboardingService#resetStuckScreening}. */
    @PostMapping("/tenants/{tenantId}/onboarding/reset")
    public TenantStatusResponse resetScreening(@PathVariable Long tenantId, @Valid @RequestBody DecisionRequest request) {
        return toResponse(onboarding.resetStuckScreening(tenantId, request.reason()));
    }

    @PostMapping("/tenants/{tenantId}/status")
    public TenantStatusResponse changeStatus(@PathVariable Long tenantId, @Valid @RequestBody StatusChangeRequest request) {
        return toResponse(lifecycle.transition(tenantId, request.targetStatus(), request.reason()));
    }

    @PostMapping("/tenants/{tenantId}/api-keys")
    public ResponseEntity<IssuedApiKey> issueKey(@PathVariable Long tenantId, @Valid @RequestBody IssueKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeys.issueKey(tenantId, request.environment()));
    }

    @PostMapping("/tenants/{tenantId}/api-keys/rotate")
    public ResponseEntity<KeyRotationResult> rotateKey(@PathVariable Long tenantId, @Valid @RequestBody RotateKeyRequest request) {
        Duration grace = request.gracePeriodHours() == null ? null : Duration.ofHours(request.gracePeriodHours());
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeys.rotate(tenantId, request.environment(), grace));
    }

    @PostMapping("/api-keys/{keyId}/revoke")
    public ResponseEntity<Void> revokeKey(@PathVariable Long keyId) {
        apiKeys.revoke(keyId);
        return ResponseEntity.noContent().build();
    }

    private static TenantStatusResponse toResponse(CorporateTenant tenant) {
        return new TenantStatusResponse(tenant.getId(), tenant.getStatus(), tenant.getKycStatus(), tenant.getRiskScore());
    }
}
