package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.WebhookDeliveryStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.WebhookDeliveryAttempt} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryAttemptDTO implements Serializable {

    private Long id;

    @NotNull
    private String eventType;

    @NotNull
    private WebhookDeliveryStatus status;

    private Integer httpStatusCode;

    @NotNull
    private Integer attemptNumber;

    @NotNull
    private Instant attemptedAt;

    @NotNull
    private WebhookSubscriptionDTO subscription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public WebhookDeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(WebhookDeliveryStatus status) {
        this.status = status;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public WebhookSubscriptionDTO getSubscription() {
        return subscription;
    }

    public void setSubscription(WebhookSubscriptionDTO subscription) {
        this.subscription = subscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookDeliveryAttemptDTO)) {
            return false;
        }

        WebhookDeliveryAttemptDTO webhookDeliveryAttemptDTO = (WebhookDeliveryAttemptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, webhookDeliveryAttemptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryAttemptDTO{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", status='" + getStatus() + "'" +
            ", httpStatusCode=" + getHttpStatusCode() +
            ", attemptNumber=" + getAttemptNumber() +
            ", attemptedAt='" + getAttemptedAt() + "'" +
            ", subscription=" + getSubscription() +
            "}";
    }
}
