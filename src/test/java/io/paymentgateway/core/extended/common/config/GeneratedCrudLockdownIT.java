package io.paymentgateway.core.extended.common.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.paymentgateway.core.domain.ApiKey;
import io.paymentgateway.core.domain.CorporateTenant;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import io.paymentgateway.core.domain.enumeration.KycStatus;
import io.paymentgateway.core.domain.enumeration.TenantStatus;
import io.paymentgateway.core.extended.tenant.support.AbstractTenantIT;
import io.paymentgateway.core.security.SecurityUtils;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.TestPropertySource;

/**
 * The generated CRUD endpoints are admin-only whenever the lockdown is active (every real runtime). Requests carry real,
 * signed JWTs minted with the application's own encoder, so the lockdown chain's decoder and authority mapping are exercised,
 * not bypassed.
 */
@TestPropertySource(properties = GeneratedCrudLockdownCondition.PROPERTY + "=true")
class GeneratedCrudLockdownIT extends AbstractTenantIT {

    @Autowired
    private JwtEncoder jwtEncoder;

    private String token(String subject, String authority, Duration validFor) {
        Instant expiresAt = Instant.now().plus(validFor);
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(subject)
            .issuedAt(expiresAt.minus(Duration.ofMinutes(10))) // an already-expired token is issued in the past
            .expiresAt(expiresAt)
            .claim(SecurityUtils.AUTHORITIES_CLAIM, authority)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims)).getTokenValue();
    }

    private String userToken() {
        return token("self-registered", "ROLE_USER", Duration.ofMinutes(5));
    }

    private String adminToken() {
        return token("staff", "ROLE_ADMIN", Duration.ofMinutes(5));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void aSelfRegisteredUserCannotEditATenantsStatusOrKycThroughTheGeneratedEndpoints() throws Exception {
        CorporateTenant tenant = newTenant(TenantStatus.PENDING_REVIEW, KycStatus.NOT_STARTED);
        String forged =
            "{\"id\":" +
            tenant.getId() +
            ",\"legalBusinessName\":\"x\",\"businessRegistrationNumber\":\"" +
            tenant.getBusinessRegistrationNumber() +
            "\",\"taxIdentificationNumber\":\"t\",\"operatingJurisdiction\":\"NG\",\"status\":\"ACTIVE\",\"kycStatus\":\"APPROVED\",\"createdAt\":\"2026-01-01T00:00:00Z\"}";

        mvc.perform(
            put("/api/corporate-tenants/" + tenant.getId())
                .header("Authorization", bearer(userToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(forged)
        ).andExpect(status().isForbidden());
        mvc.perform(
            patch("/api/corporate-tenants/" + tenant.getId())
                .header("Authorization", bearer(userToken()))
                .contentType("application/merge-patch+json")
                .content("{\"status\":\"ACTIVE\"}")
        ).andExpect(status().isForbidden());

        CorporateTenant after = reload(tenant);
        assertThat(after.getStatus()).isEqualTo(TenantStatus.PENDING_REVIEW);
        assertThat(after.getKycStatus()).isEqualTo(KycStatus.NOT_STARTED);
    }

    @Test
    void aSelfRegisteredUserCannotTouchApiKeysThroughTheGeneratedEndpoints() throws Exception {
        CorporateTenant tenant = activeTenant();
        var issued = apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE);
        apiKeyService.revoke(issued.id());

        mvc.perform(get("/api/api-keys").header("Authorization", bearer(userToken()))).andExpect(status().isForbidden());
        mvc.perform(get("/api/api-keys/" + issued.id()).header("Authorization", bearer(userToken()))).andExpect(status().isForbidden()); // would expose keyHash
        mvc.perform(
            patch("/api/api-keys/" + issued.id())
                .header("Authorization", bearer(userToken()))
                .contentType("application/merge-patch+json")
                .content("{\"isActive\":true,\"revokedAt\":null}")
        ).andExpect(status().isForbidden());
        mvc.perform(
            post("/api/api-keys").header("Authorization", bearer(userToken())).contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isForbidden());
        mvc.perform(delete("/api/api-keys/" + issued.id()).header("Authorization", bearer(userToken()))).andExpect(status().isForbidden());

        ApiKey stored = extendedKeys.findById(issued.id()).orElseThrow();
        assertThat(stored.getRevokedAt()).isNotNull(); // the revoked key stayed revoked
        assertThat(stored.getIsActive()).isFalse();
        mvc.perform(whoAmI(issued.secret())).andExpect(status().isUnauthorized());
    }

    @Test
    void everyGeneratedEntityEndpointIsAdminOnly_notJustTheTenantModule() throws Exception {
        for (String path : new String[] {
            "transactions",
            "journal-entries",
            "tenant-wallets",
            "disputes",
            "audit-log-entries",
            "routing-rules",
            "countries",
        }) {
            mvc.perform(get("/api/" + path).header("Authorization", bearer(userToken()))).andExpect(status().isForbidden());
            mvc.perform(get("/api/" + path).header("Authorization", bearer(adminToken()))).andExpect(status().isOk());
        }
    }

    @Test
    void anAdminMayStillUseTheGeneratedEndpoints() throws Exception {
        CorporateTenant tenant = activeTenant();
        mvc.perform(get("/api/corporate-tenants").header("Authorization", bearer(adminToken()))).andExpect(status().isOk());
        mvc.perform(get("/api/corporate-tenants/" + tenant.getId()).header("Authorization", bearer(adminToken()))).andExpect(
            status().isOk()
        );
    }

    @Test
    void missingExpiredOrTamperedTokensAreUnauthorised() throws Exception {
        mvc.perform(get("/api/corporate-tenants")).andExpect(status().isUnauthorized());
        // Expired well beyond Spring's default 60-second clock-skew allowance.
        String expired = token("staff", "ROLE_ADMIN", Duration.ofMinutes(-10));
        mvc.perform(get("/api/corporate-tenants").header("Authorization", bearer(expired))).andExpect(status().isUnauthorized());
        String tampered = adminToken();
        tampered = tampered.substring(0, tampered.length() - 3) + (tampered.endsWith("AAA") ? "BBB" : "AAA");
        mvc.perform(get("/api/corporate-tenants").header("Authorization", bearer(tampered))).andExpect(status().isUnauthorized());
    }

    @Test
    void endpointsThatAreNotEntityCrudAreUntouched() throws Exception {
        // A plain user reaches these, which they could not if the lockdown chain had claimed them (it demands ROLE_ADMIN).
        mvc.perform(get("/api/authenticate").header("Authorization", bearer(userToken()))).andExpect(status().is2xxSuccessful());
        mvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()); // reachable, rejects the empty body
        mvc.perform(get("/api/extended/tenants/1/none").header("Authorization", bearer(userToken()))).andExpect(status().isNotFound()); // handled by the generated chain
    }

    @Test
    void theMerchantApiKeyChainIsUnaffected() throws Exception {
        CorporateTenant tenant = activeTenant();
        String secret = apiKeyService.issueKey(tenant.getId(), ApiEnvironment.LIVE).secret();
        mvc.perform(whoAmI(secret)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/whoami").header("Authorization", bearer(adminToken()))).andExpect(status().isUnauthorized()); // a staff JWT is still not a merchant key
    }
}
