package io.paymentgateway.core.extended.tenant.service.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.service.TenantAccessGuard;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TenantLifecycleIT extends AbstractTenantIT {

    @Autowired
    private TenantLifecycleService lifecycle;

    @Autowired
    private TenantAccessGuard guard;

    private String statusUrl(CorporateTenant tenant) {
        return "/api/extended/tenants/" + tenant.getId() + "/status";
    }

    private String body(TenantStatus target) {
        return "{\"targetStatus\":\"" + target + "\",\"reason\":\"test\"}";
    }

    @Test
    void suspendingATenantBlocksItsKeysAndNewTransactions_andReinstatementRestoresThem() throws Exception {
        CorporateTenant tenant = activeTenant();
        String live = issueViaApi(tenant.getId(), ApiEnvironment.LIVE)[1];
        String test = issueViaApi(tenant.getId(), ApiEnvironment.TEST)[1];
        mvc.perform(whoAmI(live)).andExpect(status().isOk());
        guard.assertCanTransact(tenant.getId(), ApiEnvironment.LIVE); // allowed while ACTIVE

        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.SUSPENDED)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUSPENDED"));

        // The same keys are now refused: 403, not 401, because the keys themselves are still valid.
        mvc.perform(whoAmI(live)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("TENANT_SUSPENDED"));
        mvc.perform(whoAmI(test)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("TENANT_SUSPENDED"));
        for (ApiEnvironment env : ApiEnvironment.values()) {
            assertThatThrownBy(() -> guard.assertCanTransact(tenant.getId(), env)).isInstanceOfSatisfying(
                TenantOperationException.class,
                e -> assertThat(e.getCode()).isEqualTo("TENANT_SUSPENDED")
            );
        }
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).hasSize(1); // suspension does not revoke

        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.ACTIVE))).andExpect(status().isOk());
        mvc.perform(whoAmI(live)).andExpect(status().isOk());
        guard.assertCanTransact(tenant.getId(), ApiEnvironment.LIVE);
    }

    @Test
    void reinstatementKeepsTheOriginalActivationTime() throws Exception {
        CorporateTenant tenant = activeTenant();
        lifecycle.transition(tenant.getId(), TenantStatus.SUSPENDED, "test");
        assertThat(reload(tenant).getActivatedAt()).isNull(); // fixture was created ACTIVE without a timestamp
        lifecycle.transition(tenant.getId(), TenantStatus.ACTIVE, "test");
        var firstActivation = reload(tenant).getActivatedAt();
        assertThat(firstActivation).isNotNull();

        clock.advance(Duration.ofDays(3));
        lifecycle.transition(tenant.getId(), TenantStatus.SUSPENDED, "again");
        lifecycle.transition(tenant.getId(), TenantStatus.ACTIVE, "again");
        assertThat(reload(tenant).getActivatedAt()).isEqualTo(firstActivation);
    }

    @Test
    void closingATenantRevokesEveryKeyPermanently() throws Exception {
        CorporateTenant tenant = activeTenant();
        String live = issueViaApi(tenant.getId(), ApiEnvironment.LIVE)[1];
        String test = issueViaApi(tenant.getId(), ApiEnvironment.TEST)[1];

        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.CLOSED))).andExpect(status().isOk());

        mvc.perform(whoAmI(live)).andExpect(status().isUnauthorized());
        mvc.perform(whoAmI(test)).andExpect(status().isUnauthorized());
        assertThat(extendedKeys.findNotRevokedByTenant(tenant.getId())).isEmpty();
        // CLOSED is terminal: no way back, and no new keys.
        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.ACTIVE)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("INVALID_TENANT_TRANSITION"));
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/api-keys", "{\"environment\":\"TEST\"}")).andExpect(
            status().isForbidden()
        );
    }

    @Test
    void aTenantCannotBecomeActiveWithoutApprovedKyc() throws Exception {
        CorporateTenant pending = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED);
        mvc.perform(adminPost(statusUrl(pending), body(TenantStatus.ACTIVE)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_NOT_APPROVED"));
        assertThat(reload(pending).getStatus()).isEqualTo(TenantStatus.PENDING_REVIEW);

        CorporateTenant inReview = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.MANUAL_REVIEW);
        mvc.perform(adminPost(statusUrl(inReview), body(TenantStatus.ACTIVE)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_NOT_APPROVED"));
    }

    @Test
    void suspendThenReinstateCannotBypassVerification() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED);
        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.SUSPENDED))).andExpect(status().isOk());
        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.ACTIVE)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_NOT_APPROVED"));
        assertThat(reload(tenant).getStatus()).isEqualTo(TenantStatus.SUSPENDED);
    }

    @Test
    void illegalTransitionsAreRejectedWithTheAllowedOnesListed() throws Exception {
        CorporateTenant active = activeTenant();
        mvc.perform(adminPost(statusUrl(active), body(TenantStatus.PENDING_REVIEW)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("INVALID_TENANT_TRANSITION"));
        mvc.perform(adminPost(statusUrl(active), body(TenantStatus.ACTIVE))).andExpect(status().isConflict()); // no self-transition
        mvc.perform(adminPost(statusUrl(active), body(TenantStatus.REJECTED))).andExpect(status().isConflict());
        assertThat(reload(active).getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    void rejectingATenantRecordsRejectedKycAndRevokesKeys() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.MANUAL_REVIEW);
        String test = issueViaApi(tenant.getId(), ApiEnvironment.TEST)[1];
        mvc.perform(adminPost(statusUrl(tenant), body(TenantStatus.REJECTED)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.kycStatus").value("REJECTED"));
        mvc.perform(whoAmI(test)).andExpect(status().isUnauthorized());
    }

    @Test
    void pendingTenantsGetTheSandboxButNeverLive() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED);

        String test = issueViaApi(tenant.getId(), ApiEnvironment.TEST)[1];
        mvc.perform(whoAmI(test)).andExpect(status().isOk()).andExpect(jsonPath("$.environment").value("TEST"));

        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/api-keys", "{\"environment\":\"LIVE\"}"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("TENANT_NOT_VERIFIED"));
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).isEmpty();
        assertThatThrownBy(() -> guard.assertCanTransact(tenant.getId(), ApiEnvironment.LIVE)).isInstanceOfSatisfying(
            TenantOperationException.class,
            e -> assertThat(e.getCode()).isEqualTo("TENANT_NOT_VERIFIED")
        );
        guard.assertCanTransact(tenant.getId(), ApiEnvironment.TEST); // sandbox is fine
    }

    @Test
    void aLiveKeyOfATenantThatIsNoLongerActiveIsRefusedEvenIfItWasIssuedEarlier() throws Exception {
        CorporateTenant tenant = activeTenant();
        String live = issueViaApi(tenant.getId(), ApiEnvironment.LIVE)[1];
        tx.executeWithoutResult(s -> {
            CorporateTenant t = extendedTenants.findById(tenant.getId()).orElseThrow();
            t.setStatus(TenantStatus.PENDING_REVIEW); // e.g. a data fix; the guard must follow the stored status
            extendedTenants.save(t);
        });
        mvc.perform(whoAmI(live)).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("TENANT_NOT_VERIFIED"));
    }

    @Test
    void applyTransitionRefusesATenantThatIsNotLocked() {
        CorporateTenant tenant = activeTenant();
        assertThatThrownBy(() ->
            tx.executeWithoutResult(s ->
                lifecycle.applyTransition(extendedTenants.findById(tenant.getId()).orElseThrow(), TenantStatus.SUSPENDED, "no lock")
            )
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("locked");
        assertThat(reload(tenant).getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    void unknownTenantsAreNotFoundAndStatusChangesNeedAReason() throws Exception {
        mvc.perform(adminPost("/api/extended/tenants/999999999/status", body(TenantStatus.SUSPENDED))).andExpect(status().isNotFound());
        CorporateTenant tenant = activeTenant();
        mvc.perform(adminPost(statusUrl(tenant), "{\"targetStatus\":\"SUSPENDED\"}")).andExpect(status().isBadRequest());
        mvc.perform(adminPost(statusUrl(tenant), "{\"targetStatus\":\"SUSPENDED\",\"reason\":\"  \"}")).andExpect(status().isBadRequest());
        assertThat(reload(tenant).getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }

    @Test
    void keyRowsOfATenantAreRevokedNotDeletedWhenClosed() throws Exception {
        CorporateTenant tenant = activeTenant();
        String id = issueViaApi(tenant.getId(), ApiEnvironment.LIVE)[0];
        lifecycle.transition(tenant.getId(), TenantStatus.CLOSED, "offboarding");
        ApiKey key = extendedKeys.findById(Long.parseLong(id)).orElseThrow();
        assertThat(key.getRevokedAt()).isNotNull();
        assertThat(key.getIsActive()).isFalse();
    }
}
