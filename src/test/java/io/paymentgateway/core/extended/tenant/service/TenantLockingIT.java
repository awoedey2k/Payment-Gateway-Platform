package io.paymentgateway.core.extended.tenant.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.domain.IssuedApiKey;
import io.paymentgateway.core.extended.tenant.service.lifecycle.TenantLifecycleService;
import io.paymentgateway.core.extended.tenant.service.onboarding.OnboardingService;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Deterministic proof that every state-changing tenant operation serialises on the tenant row lock: while another
 * transaction holds {@code SELECT ... FOR UPDATE} on the tenant, the operation must not complete. (Random race tests can pass
 * by luck without a lock; this cannot.) Authentication, being read-only, must NOT wait.
 */
class TenantLockingIT extends AbstractTenantIT {

    private static final long GIVE_IT_A_CHANCE_TO_FINISH_MS = 800;

    @Autowired
    private TenantLifecycleService lifecycle;

    @Autowired
    private OnboardingService onboarding;

    private <T> void assertWaitsForTenantLock(Long tenantId, Callable<T> operation) throws Exception {
        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> holder = pool.submit(() ->
                tx.executeWithoutResult(status -> {
                    extendedTenants.findByIdForUpdate(tenantId).orElseThrow();
                    locked.countDown();
                    try {
                        release.await(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
            );
            assertThat(locked.await(10, TimeUnit.SECONDS)).as("lock holder started").isTrue();

            Future<T> waiting = pool.submit(operation);
            Thread.sleep(GIVE_IT_A_CHANCE_TO_FINISH_MS);
            assertThat(waiting.isDone()).as("operation must wait while another transaction holds the tenant row lock").isFalse();

            release.countDown();
            holder.get(10, TimeUnit.SECONDS);
            waiting.get(20, TimeUnit.SECONDS); // and it completes once the lock is released
        } finally {
            release.countDown();
            pool.shutdownNow();
        }
    }

    @Test
    void rotateWaitsForTheTenantLock() throws Exception {
        CorporateTenant tenant = activeTenant();
        apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);
        assertWaitsForTenantLock(tenant.getId(), () -> apiKeyService.rotate(tenant.getId(), ApiEnvironment.LIVE, Duration.ofHours(24)));
    }

    @Test
    void issueWaitsForTheTenantLock() throws Exception {
        CorporateTenant tenant = activeTenant();
        assertWaitsForTenantLock(tenant.getId(), () -> apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE));
    }

    @Test
    void revokeWaitsForTheTenantLock() throws Exception {
        CorporateTenant tenant = activeTenant();
        IssuedApiKey key = apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);
        assertWaitsForTenantLock(tenant.getId(), () -> {
            apiKeyService.revoke(key.id());
            return null;
        });
    }

    @Test
    void statusTransitionsWaitForTheTenantLock() throws Exception {
        CorporateTenant tenant = activeTenant();
        assertWaitsForTenantLock(tenant.getId(), () -> lifecycle.transition(tenant.getId(), TenantStatus.SUSPENDED, "test"));
    }

    @Test
    void kycScreeningWaitsForTheTenantLock() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED, "RC-LOCK-1");
        addDirector(tenant, "Sarah Doe");
        assertWaitsForTenantLock(tenant.getId(), () -> onboarding.screen(tenant.getId()));
    }

    @Test
    void authenticationNeverWaitsForTheTenantLock() throws Exception {
        CorporateTenant tenant = activeTenant();
        IssuedApiKey key = apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);

        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            pool.submit(() ->
                tx.executeWithoutResult(status -> {
                    extendedTenants.findByIdForUpdate(tenant.getId()).orElseThrow();
                    locked.countDown();
                    try {
                        release.await(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
            );
            assertThat(locked.await(10, TimeUnit.SECONDS)).isTrue();

            Future<Boolean> auth = pool.submit(() -> apiKeyService.authenticate(key.secret()).isPresent());
            assertThat(auth.get(5, TimeUnit.SECONDS)).as("authentication is read-only and must not queue behind writers").isTrue();
        } finally {
            release.countDown();
            pool.shutdownNow();
        }
    }
}
