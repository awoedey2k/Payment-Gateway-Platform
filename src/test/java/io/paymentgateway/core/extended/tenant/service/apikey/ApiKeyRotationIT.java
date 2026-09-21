package io.paymentgateway.core.extended.tenant.service.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

/**
 * The full API key rotation sequence of spec Chunk 2 §4.2, over HTTP against real PostgreSQL:
 * issue A, rotate to B (dual-active window), revoke A, confirm A stops authenticating.
 */
class ApiKeyRotationIT extends AbstractTenantIT {

    private static final String ROTATE = "/api/extended/tenants/%d/api-keys/rotate";

    @Test
    void fullRotationSequence_issueB_dualActiveWindow_revokeA_AStopsAuthenticating() throws Exception {
        CorporateTenant tenant = activeTenant();

        // Key A is issued; the secret is shown once and authenticates.
        String[] a = issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        String keyAId = a[0];
        String secretA = a[1];
        mvc.perform(whoAmI(secretA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").value(tenant.getId()))
            .andExpect(jsonPath("$.apiKeyId").value(Long.parseLong(keyAId)))
            .andExpect(jsonPath("$.environment").value("LIVE"));

        // Rotate: Key B is created and Key A stays valid for the 24h window.
        MvcResult rotated = mvc
            .perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\",\"gracePeriodHours\":24}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.previousKeyIds[0]").value(Long.parseLong(keyAId)))
            .andReturn();
        String secretB = read(rotated, "$.newKey.secret");
        String keyBId = read(rotated, "$.newKey.id");
        assertThat(secretB).isNotEqualTo(secretA).startsWith("sk_live_");

        // Dual-active window: both keys authenticate, each as itself.
        mvc.perform(whoAmI(secretA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apiKeyId").value(Long.parseLong(keyAId)));
        mvc.perform(whoAmI(secretB))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apiKeyId").value(Long.parseLong(keyBId)));
        clock.advance(Duration.ofHours(23));
        mvc.perform(whoAmI(secretA)).andExpect(status().isOk());

        // Merchant migrates to B and explicitly revokes A: A is refused at once, B is unaffected.
        mvc.perform(adminPost("/api/extended/api-keys/" + keyAId + "/revoke", "{}")).andExpect(status().isNoContent());
        mvc.perform(whoAmI(secretA)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_API_KEY"));
        mvc.perform(whoAmI(secretB)).andExpect(status().isOk());

        ApiKey storedA = extendedKeys.findById(Long.parseLong(keyAId)).orElseThrow();
        assertThat(storedA.getIsActive()).isFalse();
        assertThat(storedA.getRevokedAt()).isNotNull();
    }

    @Test
    void secretsAreNeverStoredOnlyTheirPepperedHash() throws Exception {
        CorporateTenant tenant = activeTenant();
        String[] key = issueViaApi(tenant.getId(), ApiEnvironment.LIVE);

        ApiKey stored = extendedKeys.findById(Long.parseLong(key[0])).orElseThrow();
        assertThat(stored.getKeyHash()).isEqualTo(hasher.hash(key[1])).doesNotContain(key[1]);
        assertThat(stored.getKeyPrefix()).isEqualTo(key[1].substring(0, 12));
        assertThat(stored.getTenant().getId()).isEqualTo(tenant.getId());
        assertThat(stored.getIsActive()).isTrue();
        assertThat(stored.getIssuedAt()).isNotNull();
    }

    @Test
    void aKeyStopsWorkingTheMomentItsGracePeriodEnds_andTheSweepFlagsItInactive() throws Exception {
        CorporateTenant tenant = activeTenant();
        String[] a = issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        MvcResult rotated = mvc
            .perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\",\"gracePeriodHours\":24}"))
            .andExpect(status().isCreated())
            .andReturn();
        String secretB = read(rotated, "$.newKey.secret");

        clock.advance(Duration.ofHours(24).minusSeconds(1));
        mvc.perform(whoAmI(a[1])).andExpect(status().isOk());

        clock.advance(Duration.ofSeconds(1)); // exactly at graceExpiresAt: no longer valid
        mvc.perform(whoAmI(a[1])).andExpect(status().isUnauthorized());
        mvc.perform(whoAmI(secretB)).andExpect(status().isOk());

        // Even before the sweep runs, the key is refused; the sweep then tidies the flag.
        assertThat(extendedKeys.findById(Long.parseLong(a[0])).orElseThrow().getIsActive()).isTrue();
        assertThat(apiKeyService.expireLapsedKeys()).isEqualTo(1);
        assertThat(extendedKeys.findById(Long.parseLong(a[0])).orElseThrow().getIsActive()).isFalse();
        assertThat(apiKeyService.expireLapsedKeys()).isZero();

        // With A gone only B is usable, so the next rotation is allowed again.
        mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\"}")).andExpect(status().isCreated());
    }

    @Test
    void anExplicitlyRevokedKeyIsRefusedImmediatelyEvenInsideItsGracePeriod() throws Exception {
        CorporateTenant tenant = activeTenant();
        String[] a = issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\",\"gracePeriodHours\":48}")).andExpect(
            status().isCreated()
        );

        mvc.perform(whoAmI(a[1])).andExpect(status().isOk());
        mvc.perform(adminPost("/api/extended/api-keys/" + a[0] + "/revoke", "{}")).andExpect(status().isNoContent());
        mvc.perform(whoAmI(a[1])).andExpect(status().isUnauthorized());

        // Revoking again is harmless and keeps the original revocation time.
        var revokedAt = extendedKeys.findById(Long.parseLong(a[0])).orElseThrow().getRevokedAt();
        clock.advance(Duration.ofMinutes(5));
        mvc.perform(adminPost("/api/extended/api-keys/" + a[0] + "/revoke", "{}")).andExpect(status().isNoContent());
        assertThat(extendedKeys.findById(Long.parseLong(a[0])).orElseThrow().getRevokedAt()).isEqualTo(revokedAt);
    }

    @Test
    void aSecondRotationIsRefusedWhileTwoKeysAreUsable() throws Exception {
        CorporateTenant tenant = activeTenant();
        issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\"}")).andExpect(status().isCreated());

        mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ROTATION_IN_PROGRESS"));
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).hasSize(2);
    }

    @Test
    void rotationAndIssueRequireTheRightStartingPoint() throws Exception {
        CorporateTenant tenant = activeTenant();
        mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("NO_ACTIVE_KEY"));

        issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        mvc.perform(adminPost("/api/extended/tenants/" + tenant.getId() + "/api-keys", "{\"environment\":\"LIVE\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ACTIVE_KEY_EXISTS"));

        // TEST and LIVE keys are independent.
        issueViaApi(tenant.getId(), ApiEnvironment.TEST);
    }

    @Test
    void theGracePeriodMustBeWithinTheConfiguredBounds() throws Exception {
        CorporateTenant tenant = activeTenant();
        issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        for (int hours : new int[] { 0, -1, 49, 500 }) {
            mvc.perform(adminPost(ROTATE.formatted(tenant.getId()), "{\"environment\":\"LIVE\",\"gracePeriodHours\":" + hours + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("GRACE_PERIOD_OUT_OF_RANGE"));
        }
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).hasSize(1);
    }

    @Test
    void aTestKeyNeverAuthenticatesAsALiveKeyAndViceVersa() throws Exception {
        CorporateTenant tenant = activeTenant();
        String[] live = issueViaApi(tenant.getId(), ApiEnvironment.LIVE);
        String[] test = issueViaApi(tenant.getId(), ApiEnvironment.TEST);
        mvc.perform(whoAmI(live[1])).andExpect(jsonPath("$.environment").value("LIVE"));
        mvc.perform(whoAmI(test[1])).andExpect(jsonPath("$.environment").value("TEST"));
        // Swapping the environment marker of a valid secret breaks it: the hash no longer matches.
        mvc.perform(whoAmI(live[1].replace("sk_live_", "sk_test_"))).andExpect(status().isUnauthorized());
    }

    @Test
    void merchantEndpointsRejectMissingMalformedAndUnknownKeys() throws Exception {
        mvc.perform(get("/api/v1/whoami")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("API_KEY_REQUIRED"));
        mvc.perform(whoAmI("garbage")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_API_KEY"));
        mvc.perform(whoAmI("sk_live_" + "a".repeat(64)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_API_KEY"));
        mvc.perform(get("/api/v1/whoami").header("Authorization", "Basic abc")).andExpect(status().isUnauthorized());
    }

    @Test
    void aStaffPrincipalIsNotAMerchantCredential() throws Exception {
        // An authenticated staff user has no merchant authority on the API-key chain.
        mvc.perform(get("/api/v1/whoami").with(admin())).andExpect(status().isForbidden());
        // A staff JWT presented as a bearer token is not an API key either.
        String jwtLooking = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.c2lnbmF0dXJl";
        mvc.perform(whoAmI(jwtLooking)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("INVALID_API_KEY"));
    }

    @Test
    void staffKeyEndpointsRequireTheAdminRole() throws Exception {
        CorporateTenant tenant = activeTenant();
        String url = "/api/extended/tenants/" + tenant.getId() + "/api-keys";
        mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType("application/json")
                .content("{\"environment\":\"LIVE\"}")
        ).andExpect(status().isUnauthorized());
        mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .with(plainUser())
                .contentType("application/json")
                .content("{\"environment\":\"LIVE\"}")
        ).andExpect(status().isForbidden());
        assertThat(extendedKeys.findActiveByTenantAndEnvironment(tenant.getId(), ApiEnvironment.LIVE)).isEmpty();
    }

    @Test
    void unknownTenantsAndKeysAreNotFound() throws Exception {
        mvc.perform(adminPost("/api/extended/tenants/999999999/api-keys", "{\"environment\":\"LIVE\"}")).andExpect(status().isNotFound());
        mvc.perform(adminPost("/api/extended/api-keys/999999999/revoke", "{}")).andExpect(status().isNotFound());
    }
}
