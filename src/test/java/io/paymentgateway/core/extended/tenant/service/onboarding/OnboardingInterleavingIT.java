package io.paymentgateway.core.extended.tenant.service.onboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.CountryCode;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.service.kyc.BusinessRegistryClient;
import io.paymentgateway.core.extended.tenant.service.kyc.ComplianceScreeningProvider;
import io.paymentgateway.core.extended.tenant.service.kyc.PartyToScreen;
import io.paymentgateway.core.extended.tenant.service.kyc.RegistryRecord;
import io.paymentgateway.core.extended.tenant.service.kyc.RegistryStatus;
import io.paymentgateway.core.extended.tenant.service.kyc.SanctionsScreeningResult;
import io.paymentgateway.core.extended.tenant.service.lifecycle.TenantLifecycleService;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * What happens when something else changes the tenant while a KYC screening is in flight. A provider double holds the
 * screening open between its two transactions, so the interleavings are deterministic rather than lucky.
 */
@Import(OnboardingInterleavingIT.HoldingProviderConfiguration.class)
class OnboardingInterleavingIT extends AbstractTenantIT {

    /** Blocks inside the registry lookup until released; everything else is clean. */
    static class HoldingProvider implements ComplianceScreeningProvider, BusinessRegistryClient {

        volatile CountDownLatch entered = new CountDownLatch(1);
        volatile CountDownLatch release = new CountDownLatch(1);

        void arm() {
            entered = new CountDownLatch(1);
            release = new CountDownLatch(1);
        }

        @Override
        public SanctionsScreeningResult screenPerson(PartyToScreen person) {
            return SanctionsScreeningResult.CLEAR;
        }

        @Override
        public SanctionsScreeningResult screenBusiness(String legalBusinessName, CountryCode jurisdiction) {
            return SanctionsScreeningResult.CLEAR;
        }

        @Override
        public RegistryRecord lookup(String registrationNumber, CountryCode jurisdiction) {
            entered.countDown();
            try {
                release.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new RegistryRecord(RegistryStatus.ACTIVE, null);
        }
    }

    @TestConfiguration
    static class HoldingProviderConfiguration {

        @Bean
        @Primary
        HoldingProvider holdingProvider() {
            return new HoldingProvider();
        }
    }

    @Autowired
    private HoldingProvider provider;

    @Autowired
    private OnboardingService onboarding;

    @Autowired
    private TenantLifecycleService lifecycle;

    private CorporateTenant pendingTenantWithDirector(String regNo) {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED, regNo);
        addDirector(tenant, "Sarah Doe");
        return tenant;
    }

    private Future<Object> screenInBackground(ExecutorService pool, Long tenantId) throws InterruptedException {
        provider.arm();
        Future<Object> screening = pool.submit(() -> onboarding.screen(tenantId));
        assertThat(provider.entered.await(10, TimeUnit.SECONDS)).as("screening reached the provider call").isTrue();
        return screening;
    }

    @Test
    void aSuspensionDuringScreeningIsNotOverriddenByAnAutoApproval() throws Exception {
        CorporateTenant tenant = pendingTenantWithDirector("RC-INTERLEAVE-1");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<Object> screening = screenInBackground(pool, tenant.getId());
            assertThat(reload(tenant).getKycStatus()).isEqualTo(KycStatus.PENDING);

            // Staff suspend the tenant while the (clean) screening is still being computed.
            lifecycle.transition(tenant.getId(), TenantStatus.SUSPENDED, "sanctions alert raised elsewhere");
            provider.release.countDown();

            assertThatThrownBy(() -> screening.get(20, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(TenantOperationException.class)
                .satisfies(e -> assertThat(((TenantOperationException) e.getCause()).getCode()).isEqualTo("TENANT_STATUS_CHANGED"));
        } finally {
            provider.release.countDown();
            pool.shutdownNow();
        }

        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.SUSPENDED); // the suspension stands
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED); // and nothing was recorded as approved
        assertThat(after.getActivatedAt()).isNull();
        assertThat(after.getRiskScore()).isNull();
    }

    @Test
    void staffCanResetAScreeningThatNeverFinished() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.PENDING, "RC-STUCK-1");
        addDirector(tenant, "Sarah Doe");
        // Stuck: every retry is refused...
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/screen", "{}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_IN_PROGRESS"));

        // ...until staff reset it.
        mvc.perform(
            adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/reset", "{\"reason\":\"process died mid-screening\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.kycStatus").value("NOT_STARTED"));
        provider.arm();
        provider.release.countDown();
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/screen", "{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("APPROVE"));
    }

    @Test
    void resetOnlyAppliesToAScreeningInProgress() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED, "RC-STUCK-2");
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/reset", "{\"reason\":\"x\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_NOT_IN_PROGRESS"));
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/reset", "{}")).andExpect(status().isBadRequest()); // reason required
    }

    @Test
    void resettingWhileAScreeningIsStillRunningDiscardsItsResult() throws Exception {
        CorporateTenant tenant = pendingTenantWithDirector("RC-INTERLEAVE-2");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<Object> screening = screenInBackground(pool, tenant.getId());
            onboarding.resetStuckScreening(tenant.getId(), "operator thought it was stuck");
            provider.release.countDown();

            assertThatThrownBy(() -> screening.get(20, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .satisfies(e -> assertThat(((TenantOperationException) e.getCause()).getCode()).isEqualTo("KYC_STATE_CHANGED"));
        } finally {
            provider.release.countDown();
            pool.shutdownNow();
        }
        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.PENDING_REVIEW);
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED);
    }
}
