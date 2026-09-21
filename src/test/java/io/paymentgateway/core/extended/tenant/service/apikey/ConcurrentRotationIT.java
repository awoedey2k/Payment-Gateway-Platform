package io.paymentgateway.core.extended.tenant.service.apikey;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.extended.tenant.domain.KeyRotationResult;
import io.paymentgateway.core.extended.tenant.service.TenantOperationException;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

/** The per-tenant row lock must keep "at most two usable keys" true when rotations race. */
class ConcurrentRotationIT extends AbstractTenantIT {

    private static final int THREADS = 8;
    private static final int ROUNDS = 25;

    @Test
    void racingRotationsOfOneTenantProduceExactlyOneNewKey() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            CorporateTenant tenant = activeTenant();
            apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);

            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<Object>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                futures.add(
                    pool.submit(() -> {
                        start.await();
                        try {
                            return apiKeyService.rotate(tenant.getId(), ApiEnvironment.LIVE, Duration.ofHours(24));
                        } catch (TenantOperationException e) {
                            return e;
                        }
                    })
                );
            }
            start.countDown();
            int succeeded = 0;
            for (Future<Object> f : futures) {
                Object outcome = f.get();
                if (outcome instanceof KeyRotationResult) {
                    succeeded++;
                } else {
                    assertThat(((TenantOperationException) outcome).getCode()).isEqualTo("ROTATION_IN_PROGRESS");
                }
            }
            pool.shutdown();

            assertThat(succeeded).as("round %d", round).isEqualTo(1);
            assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).as("round %d", round).hasSize(2);
        }
    }

    @Test
    void racingIssuesOfTheFirstKeyProduceExactlyOneKey() throws Exception {
        for (int round = 0; round < ROUNDS; round++) {
            CorporateTenant tenant = activeTenant();
            ExecutorService pool = Executors.newFixedThreadPool(THREADS);
            CountDownLatch start = new CountDownLatch(1);
            List<Future<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                futures.add(
                    pool.submit(() -> {
                        start.await();
                        try {
                            apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);
                            return true;
                        } catch (TenantOperationException e) {
                            assertThat(e.getCode()).isEqualTo("ACTIVE_KEY_EXISTS");
                            return false;
                        }
                    })
                );
            }
            start.countDown();
            int succeeded = 0;
            for (Future<Boolean> f : futures) {
                if (f.get()) {
                    succeeded++;
                }
            }
            pool.shutdown();

            assertThat(succeeded).as("round %d", round).isEqualTo(1);
            assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).as("round %d", round).hasSize(1);
        }
    }

    @Test
    void differentTenantsRotateIndependentlyWithoutBlockingEachOther() throws Exception {
        List<CorporateTenant> tenants = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            CorporateTenant t = activeTenant();
            apiKeyService.issueKey(t.getId(), ApiEnvironment.LIVE);
            tenants.add(t);
        }
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<KeyRotationResult>> futures = new ArrayList<>();
        for (CorporateTenant t : tenants) {
            futures.add(
                pool.submit(() -> {
                    start.await();
                    return apiKeyService.rotate(t.getId(), ApiEnvironment.LIVE, null);
                })
            );
        }
        start.countDown();
        for (Future<KeyRotationResult> f : futures) {
            assertThat(f.get().newKey().secret()).startsWith("sk_live_");
        }
        pool.shutdown();
        for (CorporateTenant t : tenants) {
            assertThat(extendedKeys.findActiveByTenantAndEnvironment(t.getId(), ApiEnvironment.LIVE)).hasSize(2);
        }
    }

    @Test
    void aRevokeRacingARotationNeverLeavesMoreThanTwoUsableKeysOrLosesTheRevocation() throws Exception {
        CorporateTenant tenant = activeTenant();
        var first = apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Future<?> rotate = pool.submit(() -> {
            start.await();
            return apiKeyService.rotate(tenant.getId(), ApiEnvironment.LIVE, Duration.ofHours(24));
        });
        Future<?> revoke = pool.submit(() -> {
            start.await();
            apiKeyService.revoke(first.id());
            return null;
        });
        start.countDown();
        revoke.get();
        try {
            rotate.get();
        } catch (java.util.concurrent.ExecutionException e) {
            // revoke won the race, leaving no usable key to rotate: a clean NO_ACTIVE_KEY conflict is the correct outcome
            assertThat(((TenantOperationException) e.getCause()).getCode()).isEqualTo("NO_ACTIVE_KEY");
        }
        pool.shutdown();

        assertThat(extendedKeys.findById(first.id()).orElseThrow().getRevokedAt()).isNotNull();
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE).size()).isLessThanOrEqualTo(1);
    }
}
