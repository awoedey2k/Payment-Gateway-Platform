package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.WebhookDeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WebhookDeliveryAttempt.
 */
@Entity
@Table(name = "webhook_delivery_attempt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookDeliveryAttempt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WebhookDeliveryStatus status;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @NotNull
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @NotNull
    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private WebhookSubscription subscription;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WebhookDeliveryAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return this.eventType;
    }

    public WebhookDeliveryAttempt eventType(String eventType) {
        this.setEventType(eventType);
        return this;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public WebhookDeliveryStatus getStatus() {
        return this.status;
    }

    public WebhookDeliveryAttempt status(WebhookDeliveryStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WebhookDeliveryStatus status) {
        this.status = status;
    }

    public Integer getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public WebhookDeliveryAttempt httpStatusCode(Integer httpStatusCode) {
        this.setHttpStatusCode(httpStatusCode);
        return this;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Integer getAttemptNumber() {
        return this.attemptNumber;
    }

    public WebhookDeliveryAttempt attemptNumber(Integer attemptNumber) {
        this.setAttemptNumber(attemptNumber);
        return this;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Instant getAttemptedAt() {
        return this.attemptedAt;
    }

    public WebhookDeliveryAttempt attemptedAt(Instant attemptedAt) {
        this.setAttemptedAt(attemptedAt);
        return this;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public WebhookSubscription getSubscription() {
        return this.subscription;
    }

    public void setSubscription(WebhookSubscription webhookSubscription) {
        this.subscription = webhookSubscription;
    }

    public WebhookDeliveryAttempt subscription(WebhookSubscription webhookSubscription) {
        this.setSubscription(webhookSubscription);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookDeliveryAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((WebhookDeliveryAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookDeliveryAttempt{" +
            "id=" + getId() +
            ", eventType='" + getEventType() + "'" +
            ", status='" + getStatus() + "'" +
            ", httpStatusCode=" + getHttpStatusCode() +
            ", attemptNumber=" + getAttemptNumber() +
            ", attemptedAt='" + getAttemptedAt() + "'" +
            "}";
    }
}
