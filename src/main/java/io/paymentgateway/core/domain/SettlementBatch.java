package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.SettlementBatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SettlementBatch.
 */
@Entity
@Table(name = "settlement_batch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SettlementBatch implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementBatchStatus status;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @NotNull
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SettlementBatch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public SettlementBatch reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public SettlementBatchStatus getStatus() {
        return this.status;
    }

    public SettlementBatch status(SettlementBatchStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SettlementBatchStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public SettlementBatch totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public SettlementBatch currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Instant getScheduledAt() {
        return this.scheduledAt;
    }

    public SettlementBatch scheduledAt(Instant scheduledAt) {
        this.setScheduledAt(scheduledAt);
        return this;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public SettlementBatch completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public SettlementBatch tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettlementBatch)) {
            return false;
        }
        return getId() != null && getId().equals(((SettlementBatch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SettlementBatch{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", scheduledAt='" + getScheduledAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            "}";
    }
}
