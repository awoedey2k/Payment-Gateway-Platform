package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.WebhookDeliveryStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.WebhookDeliveryAttempt} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.WebhookDeliveryAttemptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /webhook-delivery-attempts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryAttemptCriteria implements Serializable, Criteria {

    /**
     * Class for filtering WebhookDeliveryStatus
     */
    public static class WebhookDeliveryStatusFilter extends Filter<WebhookDeliveryStatus> {

        public WebhookDeliveryStatusFilter() {}

        public WebhookDeliveryStatusFilter(WebhookDeliveryStatusFilter filter) {
            super(filter);
        }

        @Override
        public WebhookDeliveryStatusFilter copy() {
            return new WebhookDeliveryStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter eventType;

    private WebhookDeliveryStatusFilter status;

    private IntegerFilter httpStatusCode;

    private IntegerFilter attemptNumber;

    private InstantFilter attemptedAt;

    private LongFilter subscriptionId;

    private Boolean distinct;

    public WebhookDeliveryAttemptCriteria() {}

    public WebhookDeliveryAttemptCriteria(WebhookDeliveryAttemptCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.eventType = other.optionalEventType().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(WebhookDeliveryStatusFilter::copy).orElse(null);
        this.httpStatusCode = other.optionalHttpStatusCode().map(IntegerFilter::copy).orElse(null);
        this.attemptNumber = other.optionalAttemptNumber().map(IntegerFilter::copy).orElse(null);
        this.attemptedAt = other.optionalAttemptedAt().map(InstantFilter::copy).orElse(null);
        this.subscriptionId = other.optionalSubscriptionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WebhookDeliveryAttemptCriteria copy() {
        return new WebhookDeliveryAttemptCriteria(this);
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

    public StringFilter getEventType() {
        return eventType;
    }

    public Optional<StringFilter> optionalEventType() {
        return Optional.ofNullable(eventType);
    }

    public StringFilter eventType() {
        if (eventType == null) {
            setEventType(new StringFilter());
        }
        return eventType;
    }

    public void setEventType(StringFilter eventType) {
        this.eventType = eventType;
    }

    public WebhookDeliveryStatusFilter getStatus() {
        return status;
    }

    public Optional<WebhookDeliveryStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public WebhookDeliveryStatusFilter status() {
        if (status == null) {
            setStatus(new WebhookDeliveryStatusFilter());
        }
        return status;
    }

    public void setStatus(WebhookDeliveryStatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getHttpStatusCode() {
        return httpStatusCode;
    }

    public Optional<IntegerFilter> optionalHttpStatusCode() {
        return Optional.ofNullable(httpStatusCode);
    }

    public IntegerFilter httpStatusCode() {
        if (httpStatusCode == null) {
            setHttpStatusCode(new IntegerFilter());
        }
        return httpStatusCode;
    }

    public void setHttpStatusCode(IntegerFilter httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public IntegerFilter getAttemptNumber() {
        return attemptNumber;
    }

    public Optional<IntegerFilter> optionalAttemptNumber() {
        return Optional.ofNullable(attemptNumber);
    }

    public IntegerFilter attemptNumber() {
        if (attemptNumber == null) {
            setAttemptNumber(new IntegerFilter());
        }
        return attemptNumber;
    }

    public void setAttemptNumber(IntegerFilter attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public InstantFilter getAttemptedAt() {
        return attemptedAt;
    }

    public Optional<InstantFilter> optionalAttemptedAt() {
        return Optional.ofNullable(attemptedAt);
    }

    public InstantFilter attemptedAt() {
        if (attemptedAt == null) {
            setAttemptedAt(new InstantFilter());
        }
        return attemptedAt;
    }

    public void setAttemptedAt(InstantFilter attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public LongFilter getSubscriptionId() {
        return subscriptionId;
    }

    public Optional<LongFilter> optionalSubscriptionId() {
        return Optional.ofNullable(subscriptionId);
    }

    public LongFilter subscriptionId() {
        if (subscriptionId == null) {
            setSubscriptionId(new LongFilter());
        }
        return subscriptionId;
    }

    public void setSubscriptionId(LongFilter subscriptionId) {
        this.subscriptionId = subscriptionId;
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
        final WebhookDeliveryAttemptCriteria that = (WebhookDeliveryAttemptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(eventType, that.eventType) &&
            Objects.equals(status, that.status) &&
            Objects.equals(httpStatusCode, that.httpStatusCode) &&
            Objects.equals(attemptNumber, that.attemptNumber) &&
            Objects.equals(attemptedAt, that.attemptedAt) &&
            Objects.equals(subscriptionId, that.subscriptionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType, status, httpStatusCode, attemptNumber, attemptedAt, subscriptionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryAttemptCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEventType().map(f -> "eventType=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalHttpStatusCode().map(f -> "httpStatusCode=" + f + ", ").orElse("") +
            optionalAttemptNumber().map(f -> "attemptNumber=" + f + ", ").orElse("") +
            optionalAttemptedAt().map(f -> "attemptedAt=" + f + ", ").orElse("") +
            optionalSubscriptionId().map(f -> "subscriptionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
