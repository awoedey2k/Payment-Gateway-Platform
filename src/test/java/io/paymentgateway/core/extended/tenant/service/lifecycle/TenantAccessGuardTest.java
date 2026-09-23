package io.paymentgateway.core.extended.tenant.service.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.domain.TenantAccessDecision;
import io.paymentgateway.core.extended.tenant.service.TenantAccessGuard;
import org.junit.jupiter.api.Test;

class TenantAccessGuardTest {

    @Test
    void activeTenantsMayUseBothEnvironments() {
        assertThat(TenantAccessGuard.decide(ApiEnvironment.LIVE, TenantStatus.ACTIVE)).isEqualTo(TenantAccessDecision.ALLOWED);
        assertThat(TenantAccessGuard.decide(ApiEnvironment.TEST, TenantStatus.ACTIVE)).isEqualTo(TenantAccessDecision.ALLOWED);
    }

    @Test
    void pendingTenantsGetTheSandboxButNeverLive() {
        assertThat(TenantAccessGuard.decide(ApiEnvironment.TEST, TenantStatus.PENDING_REVIEW)).isEqualTo(TenantAccessDecision.ALLOWED);
        TenantAccessDecision live = TenantAccessGuard.decide(ApiEnvironment.LIVE, TenantStatus.PENDING_REVIEW);
        assertThat(live).isEqualTo(TenantAccessDecision.TENANT_NOT_VERIFIED);
        assertThat(live.errorCode()).isEqualTo("TENANT_NOT_VERIFIED");
    }

    @Test
    void suspendedRejectedAndClosedTenantsAreRefusedInBothEnvironments() {
        for (ApiEnvironment env : ApiEnvironment.values()) {
            assertThat(TenantAccessGuard.decide(env, TenantStatus.SUSPENDED)).isEqualTo(TenantAccessDecision.TENANT_SUSPENDED);
            assertThat(TenantAccessGuard.decide(env, TenantStatus.REJECTED)).isEqualTo(TenantAccessDecision.TENANT_REJECTED);
            assertThat(TenantAccessGuard.decide(env, TenantStatus.CLOSED)).isEqualTo(TenantAccessDecision.TENANT_CLOSED);
        }
    }

    @Test
    void onlyAllowedHasNoErrorCode() {
        for (TenantAccessDecision d : TenantAccessDecision.values()) {
            assertThat(d.errorCode() == null).isEqualTo(d.isAllowed());
        }
    }
}
