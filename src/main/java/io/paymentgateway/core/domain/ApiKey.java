package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApiKey.
 */
@Entity
@Table(name = "api_key")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiKey implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "key_prefix", nullable = false)
    private String keyPrefix;

    @NotNull
    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false)
    private ApiEnvironment environment;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "grace_expires_at")
    private Instant graceExpiresAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ApiKey id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public ApiKey keyPrefix(String keyPrefix) {
        this.setKeyPrefix(keyPrefix);
        return this;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyHash() {
        return this.keyHash;
    }

    public ApiKey keyHash(String keyHash) {
        this.setKeyHash(keyHash);
        return this;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public ApiEnvironment getEnvironment() {
        return this.environment;
    }

    public ApiKey environment(ApiEnvironment environment) {
        this.setEnvironment(environment);
        return this;
    }

    public void setEnvironment(ApiEnvironment environment) {
        this.environment = environment;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public ApiKey isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    public ApiKey issuedAt(Instant issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getRevokedAt() {
        return this.revokedAt;
    }

    public ApiKey revokedAt(Instant revokedAt) {
        this.setRevokedAt(revokedAt);
        return this;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Instant getGraceExpiresAt() {
        return this.graceExpiresAt;
    }

    public ApiKey graceExpiresAt(Instant graceExpiresAt) {
        this.setGraceExpiresAt(graceExpiresAt);
        return this;
    }

    public void setGraceExpiresAt(Instant graceExpiresAt) {
        this.graceExpiresAt = graceExpiresAt;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public ApiKey tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiKey)) {
            return false;
        }
        return getId() != null && getId().equals(((ApiKey) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiKey{" +
            "id=" + getId() +
            ", keyPrefix='" + getKeyPrefix() + "'" +
            ", keyHash='" + getKeyHash() + "'" +
            ", environment='" + getEnvironment() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", revokedAt='" + getRevokedAt() + "'" +
            ", graceExpiresAt='" + getGraceExpiresAt() + "'" +
            "}";
    }
}
