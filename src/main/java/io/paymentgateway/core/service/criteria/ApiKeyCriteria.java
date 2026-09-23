package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.ApiEnvironment;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.ApiKey} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.ApiKeyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /api-keys?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApiKeyCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ApiEnvironment
     */
    public static class ApiEnvironmentFilter extends Filter<ApiEnvironment> {

        public ApiEnvironmentFilter() {}

        public ApiEnvironmentFilter(ApiEnvironmentFilter filter) {
            super(filter);
        }

        @Override
        public ApiEnvironmentFilter copy() {
            return new ApiEnvironmentFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter keyPrefix;

    private StringFilter keyHash;

    private ApiEnvironmentFilter environment;

    private BooleanFilter isActive;

    private InstantFilter issuedAt;

    private InstantFilter revokedAt;

    private InstantFilter graceExpiresAt;

    private LongFilter tenantId;

    private Boolean distinct;

    public ApiKeyCriteria() {}

    public ApiKeyCriteria(ApiKeyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.keyPrefix = other.optionalKeyPrefix().map(StringFilter::copy).orElse(null);
        this.keyHash = other.optionalKeyHash().map(StringFilter::copy).orElse(null);
        this.environment = other.optionalEnvironment().map(ApiEnvironmentFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.issuedAt = other.optionalIssuedAt().map(InstantFilter::copy).orElse(null);
        this.revokedAt = other.optionalRevokedAt().map(InstantFilter::copy).orElse(null);
        this.graceExpiresAt = other.optionalGraceExpiresAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ApiKeyCriteria copy() {
        return new ApiKeyCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getKeyPrefix() {
        return keyPrefix;
    }

    public Optional<StringFilter> optionalKeyPrefix() {
        return Optional.ofNullable(keyPrefix);
    }

    public StringFilter keyPrefix() {
        if (keyPrefix == null) {
            setKeyPrefix(new StringFilter());
        }
        return keyPrefix;
    }

    public void setKeyPrefix(StringFilter keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public StringFilter getKeyHash() {
        return keyHash;
    }

    public Optional<StringFilter> optionalKeyHash() {
        return Optional.ofNullable(keyHash);
    }

    public StringFilter keyHash() {
        if (keyHash == null) {
            setKeyHash(new StringFilter());
        }
        return keyHash;
    }

    public void setKeyHash(StringFilter keyHash) {
        this.keyHash = keyHash;
    }

    public ApiEnvironmentFilter getEnvironment() {
        return environment;
    }

    public Optional<ApiEnvironmentFilter> optionalEnvironment() {
        return Optional.ofNullable(environment);
    }

    public ApiEnvironmentFilter environment() {
        if (environment == null) {
            setEnvironment(new ApiEnvironmentFilter());
        }
        return environment;
    }

    public void setEnvironment(ApiEnvironmentFilter environment) {
        this.environment = environment;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
    }

    public InstantFilter getIssuedAt() {
        return issuedAt;
    }

    public Optional<InstantFilter> optionalIssuedAt() {
        return Optional.ofNullable(issuedAt);
    }

    public InstantFilter issuedAt() {
        if (issuedAt == null) {
            setIssuedAt(new InstantFilter());
        }
        return issuedAt;
    }

    public void setIssuedAt(InstantFilter issuedAt) {
        this.issuedAt = issuedAt;
    }

    public InstantFilter getRevokedAt() {
        return revokedAt;
    }

    public Optional<InstantFilter> optionalRevokedAt() {
        return Optional.ofNullable(revokedAt);
    }

    public InstantFilter revokedAt() {
        if (revokedAt == null) {
            setRevokedAt(new InstantFilter());
        }
        return revokedAt;
    }

    public void setRevokedAt(InstantFilter revokedAt) {
        this.revokedAt = revokedAt;
    }

    public InstantFilter getGraceExpiresAt() {
        return graceExpiresAt;
    }

    public Optional<InstantFilter> optionalGraceExpiresAt() {
        return Optional.ofNullable(graceExpiresAt);
    }

    public InstantFilter graceExpiresAt() {
        if (graceExpiresAt == null) {
            setGraceExpiresAt(new InstantFilter());
        }
        return graceExpiresAt;
    }

    public void setGraceExpiresAt(InstantFilter graceExpiresAt) {
        this.graceExpiresAt = graceExpiresAt;
    }

    public LongFilter getTenantId() {
        return tenantId;
    }

    public Optional<LongFilter> optionalTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public LongFilter tenantId() {
        if (tenantId == null) {
            setTenantId(new LongFilter());
        }
        return tenantId;
    }

    public void setTenantId(LongFilter tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ApiKeyCriteria that = (ApiKeyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(keyPrefix, that.keyPrefix) &&
            Objects.equals(keyHash, that.keyHash) &&
            Objects.equals(environment, that.environment) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(issuedAt, that.issuedAt) &&
            Objects.equals(revokedAt, that.revokedAt) &&
            Objects.equals(graceExpiresAt, that.graceExpiresAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyPrefix, keyHash, environment, isActive, issuedAt, revokedAt, graceExpiresAt, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApiKeyCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalKeyPrefix().map(f -> "keyPrefix=" + f + ", ").orElse("") +
            optionalKeyHash().map(f -> "keyHash=" + f + ", ").orElse("") +
            optionalEnvironment().map(f -> "environment=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalIssuedAt().map(f -> "issuedAt=" + f + ", ").orElse("") +
            optionalRevokedAt().map(f -> "revokedAt=" + f + ", ").orElse("") +
            optionalGraceExpiresAt().map(f -> "graceExpiresAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
