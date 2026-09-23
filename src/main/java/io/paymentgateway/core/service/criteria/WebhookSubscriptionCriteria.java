package io.paymentgateway.core.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.WebhookSubscription} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.WebhookSubscriptionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /webhook-subscriptions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookSubscriptionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter targetUrl;

    private StringFilter secretHash;

    private BooleanFilter isActive;

    private LongFilter tenantId;

    private Boolean distinct;

    public WebhookSubscriptionCriteria() {}

    public WebhookSubscriptionCriteria(WebhookSubscriptionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.targetUrl = other.optionalTargetUrl().map(StringFilter::copy).orElse(null);
        this.secretHash = other.optionalSecretHash().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WebhookSubscriptionCriteria copy() {
        return new WebhookSubscriptionCriteria(this);
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

    public StringFilter getTargetUrl() {
        return targetUrl;
    }

    public Optional<StringFilter> optionalTargetUrl() {
        return Optional.ofNullable(targetUrl);
    }

    public StringFilter targetUrl() {
        if (targetUrl == null) {
            setTargetUrl(new StringFilter());
        }
        return targetUrl;
    }

    public void setTargetUrl(StringFilter targetUrl) {
        this.targetUrl = targetUrl;
    }

    public StringFilter getSecretHash() {
        return secretHash;
    }

    public Optional<StringFilter> optionalSecretHash() {
        return Optional.ofNullable(secretHash);
    }

    public StringFilter secretHash() {
        if (secretHash == null) {
            setSecretHash(new StringFilter());
        }
        return secretHash;
    }

    public void setSecretHash(StringFilter secretHash) {
        this.secretHash = secretHash;
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
        final WebhookSubscriptionCriteria that = (WebhookSubscriptionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(targetUrl, that.targetUrl) &&
            Objects.equals(secretHash, that.secretHash) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetUrl, secretHash, isActive, tenantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookSubscriptionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTargetUrl().map(f -> "targetUrl=" + f + ", ").orElse("") +
            optionalSecretHash().map(f -> "secretHash=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
