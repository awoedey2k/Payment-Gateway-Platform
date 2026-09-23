package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.ApiKey} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiKeyDTO implements Serializable {

    private Long id;

    @NotNull
    private String keyPrefix;

    @NotNull
    private String keyHash;

    @NotNull
    private ApiEnvironment environment;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant issuedAt;

    private Instant revokedAt;

    private Instant graceExpiresAt;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public ApiEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ApiEnvironment environment) {
        this.environment = environment;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Instant getGraceExpiresAt() {
        return graceExpiresAt;
    }

    public void setGraceExpiresAt(Instant graceExpiresAt) {
        this.graceExpiresAt = graceExpiresAt;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiKeyDTO)) {
            return false;
        }

        ApiKeyDTO apiKeyDTO = (ApiKeyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, apiKeyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiKeyDTO{" +
            "id=" + getId() +
            ", keyPrefix='" + getKeyPrefix() + "'" +
            ", keyHash='" + getKeyHash() + "'" +
            ", environment='" + getEnvironment() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", graceExpiresAt='" + getGraceExpiresAt() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
