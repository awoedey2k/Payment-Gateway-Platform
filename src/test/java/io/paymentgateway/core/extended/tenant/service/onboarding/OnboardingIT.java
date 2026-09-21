package io.paymentgateway.core.extended.tenant.service.onboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.domain.KycAssessment;
import io.paymentgateway.core.extended.tenant.domain.KycDecision;
import io.paymentgateway.core.extended.tenant.domain.KycSignal;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The three KYC bands of spec Figure 2.2 end to end, driven through the deterministic stub provider
 * (see {@code StubKycProvider} for the trigger words).
 */
class OnboardingIT extends AbstractTenantIT {

    @Autowired
    private OnboardingService onboarding;

    private CorporateTenant pendingTenant(String registrationNumber, String... directorNames) {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED, registrationNumber);
        for (String name : directorNames) {
            addDirector(tenant, name);
        }
        return tenant;
    }

    private String screenUrl(CorporateTenant tenant) {
        return "/api/extended/tenants/" + tenant.getId() + "/onboarding/screen";
    }

    @Test
    void cleanApplicationIsAutoApprovedAndTheTenantBecomesActive() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-CLEAN-1", "Sarah Doe");

        mvc.perform(adminPost(screenUrl(tenant), "{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(0))
            .andExpect(jsonPath("$.decision").value("APPROVE"))
            .andExpect(jsonPath("$.signals").isEmpty());

        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.ACTIVE);
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.APPROVED);
        assertThat(after.getRiskScore()).isZero();
        assertThat(after.getActivatedAt()).isNotNull();

        // Activation unlocks LIVE key issuance (secrets are issued on request so they can be shown once).
        String live = issueViaApi(tenant.getId(), ApiEnvironment.LIVE)[1];
        mvc.perform(whoAmI(live)).andExpect(status().isOk());
    }

    @Test
    void mediumRiskApplicationGoesToManualReviewAndStaysPending() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-PEP-1", "Sarah Doe", "PEP Politician"); // PEP hit = 40

        mvc.perform(adminPost(screenUrl(tenant), "{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(40))
            .andExpect(jsonPath("$.decision").value("MANUAL_REVIEW"))
            .andExpect(jsonPath("$.signals[0]").value("PEP_HIT"));

        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.PENDING_REVIEW);
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.MANUAL_REVIEW);
        assertThat(after.getRiskScore()).isEqualTo(40);
        assertThat(after.getActivatedAt()).isNull();
    }

    @Test
    void criticalApplicationIsRejectedImmediately() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-BAD-1", "Sarah Doe", "SANCTIONED Person");

        mvc.perform(adminPost(screenUrl(tenant), "{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("REJECT"))
            .andExpect(jsonPath("$.score").value(70));

        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.REJECTED);
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.REJECTED);
        assertThat(after.getRiskScore()).isGreaterThanOrEqualTo(61);
        assertThat(after.getActivatedAt()).isNull();
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/api-keys", "{\"environment\":\"LIVE\"}")).andExpect(
            status().isForbidden()
        );
    }

    @Test
    void aDissolvedCompanyIsRejected() {
        CorporateTenant tenant = pendingTenant("DISSOLVED-1", "Sarah Doe");
        KycAssessment result = onboarding.screen(tenant.getId());
        assertThat(result.decision()).isEqualTo(KycDecision.REJECT);
        assertThat(result.signals()).containsExactly(KycSignal.REGISTRY_DISSOLVED);
        assertThat(reload(tenant).getStatus()).isEqualTo(TenantStatus.REJECTED);
    }

    @Test
    void registryProblemsThatAreNotConclusiveGoToManualReview() {
        assertThat(onboarding.screen(pendingTenant("UNKNOWN-1", "Sarah Doe").getId()).signals()).containsExactly(
            KycSignal.REGISTRY_NOT_FOUND
        );
        assertThat(onboarding.screen(pendingTenant("MISMATCH-1", "Sarah Doe").getId()).signals()).containsExactly(KycSignal.NAME_MISMATCH);
        KycAssessment outage = onboarding.screen(pendingTenant("UNAVAILABLE-1", "Sarah Doe").getId());
        assertThat(outage.signals()).containsExactly(KycSignal.REGISTRY_UNAVAILABLE);
        assertThat(outage.decision()).isEqualTo(KycDecision.MANUAL_REVIEW); // an outage never approves
    }

    @Test
    void noDirectorsMeansNothingCouldBeScreenedSoItGoesToManualReview() {
        KycAssessment result = onboarding.screen(pendingTenant("RC-NODIR-1").getId());
        assertThat(result.signals()).containsExactly(KycSignal.NO_DIRECTORS);
        assertThat(result.decision()).isEqualTo(KycDecision.MANUAL_REVIEW);
    }

    @Test
    void complianceCanApproveAnApplicationHeldForReview() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-PEP-2", "PEP Politician");
        onboarding.screen(tenant.getId());

        mvc.perform(
            adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/approve", "{\"reason\":\"documents verified by phone\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.kycStatus").value("APPROVED"))
            .andExpect(jsonPath("$.riskScore").value(40));
        assertThat(reload(tenant).getActivatedAt()).isNotNull();
    }

    @Test
    void complianceCanRejectAnApplicationHeldForReview() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-PEP-3", "PEP Politician");
        onboarding.screen(tenant.getId());

        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/onboarding/reject", "{\"reason\":\"source of funds unclear\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REJECTED"))
            .andExpect(jsonPath("$.kycStatus").value("REJECTED"));
    }

    @Test
    void manualDecisionsOnlyApplyToApplicationsInManualReview() throws Exception {
        CorporateTenant untouched = pendingTenant("RC-CLEAN-2", "Sarah Doe");
        mvc.perform(adminPost("/api/extended/tenants/" + untouched.getId() + "/onboarding/approve", "{\"reason\":\"x\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("NOT_IN_MANUAL_REVIEW"));
        assertThat(reload(untouched).getStatus()).isEqualTo(TenantStatus.PENDING_REVIEW);

        CorporateTenant rejected = pendingTenant("RC-BAD-2", "SANCTIONED Person");
        onboarding.screen(rejected.getId());
        mvc.perform(adminPost("/api/extended/tenants/" + rejected.getId() + "/onboarding/approve", "{\"reason\":\"override\"}")).andExpect(
            status().isConflict()
        ); // a sanctions rejection cannot be approved by hand
        assertThat(reload(rejected).getStatus()).isEqualTo(TenantStatus.REJECTED);
    }

    @Test
    void anApplicationIsScreenedOnlyOnce() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-CLEAN-3", "Sarah Doe");
        onboarding.screen(tenant.getId());
        mvc.perform(adminPost(screenUrl(tenant), "{}")).andExpect(status().isConflict()); // already ACTIVE

        CorporateTenant review = pendingTenant("RC-PEP-4", "PEP Politician");
        onboarding.screen(review.getId());
        mvc.perform(adminPost(screenUrl(review), "{}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("KYC_ALREADY_DECIDED"));
    }

    @Test
    void concurrentScreeningsOfTheSameTenantProduceExactlyOneDecision() throws Exception {
        CorporateTenant tenant = pendingTenant("RC-RACE-1", "Sarah Doe");
        int threads = 6;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<String>> outcomes = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Callable<String> task = () -> {
                start.await();
                try {
                    onboarding.screen(tenant.getId());
                    return "OK";
                } catch (TenantOperationException e) {
                    return e.getCode();
                }
            };
            outcomes.add(pool.submit(task));
        }
        start.countDown();
        List<String> results = new ArrayList<>();
        for (Future<String> f : outcomes) {
            results.add(f.get());
        }
        pool.shutdown();

        assertThat(results.stream().filter("OK"::equals)).hasSize(1);
        assertThat(results.stream().filter(r -> !r.equals("OK"))).allMatch(
            r -> r.equals("KYC_IN_PROGRESS") || r.equals("KYC_ALREADY_DECIDED") || r.equals("TENANT_NOT_PENDING_REVIEW")
        );
        assertThat(reload(tenant).getStatus()).isEqualTo(TenantStatus.ACTIVE);
    }
}
