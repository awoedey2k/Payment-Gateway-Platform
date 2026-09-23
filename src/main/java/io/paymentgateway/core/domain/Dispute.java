package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.DisputeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Dispute.
 */
@Entity
@Table(name = "dispute")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dispute implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "case_reference", nullable = false, unique = true)
    private String caseReference;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @NotNull
    @Column(name = "reason_code", nullable = false)
    private String reasonCode;

    @Column(name = "reason_description")
    private String reasonDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DisputeStatus status;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "evidence_submitted_at")
    private Instant evidenceSubmittedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "refunds", "tenant" }, allowSetters = true)
    private Transaction transaction;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dispute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseReference() {
        return this.caseReference;
    }

    public Dispute caseReference(String caseReference) {
        this.setCaseReference(caseReference);
        return this;
    }

    public void setCaseReference(String caseReference) {
        this.caseReference = caseReference;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Dispute amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Dispute currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getReasonCode() {
        return this.reasonCode;
    }

    public Dispute reasonCode(String reasonCode) {
        this.setReasonCode(reasonCode);
        return this;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return this.reasonDescription;
    }

    public Dispute reasonDescription(String reasonDescription) {
        this.setReasonDescription(reasonDescription);
        return this;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public DisputeStatus getStatus() {
        return this.status;
    }

    public Dispute status(DisputeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Dispute dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getEvidenceSubmittedAt() {
        return this.evidenceSubmittedAt;
    }

    public Dispute evidenceSubmittedAt(Instant evidenceSubmittedAt) {
        this.setEvidenceSubmittedAt(evidenceSubmittedAt);
        return this;
    }

    public void setEvidenceSubmittedAt(Instant evidenceSubmittedAt) {
        this.evidenceSubmittedAt = evidenceSubmittedAt;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public Dispute resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public Dispute tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Dispute transaction(Transaction transaction) {
        this.setTransaction(transaction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dispute)) {
            return false;
        }
        return getId() != null && getId().equals(((Dispute) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dispute{" +
            "id=" + getId() +
            ", caseReference='" + getCaseReference() + "'" +
            ", amount=" + getAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", reasonCode='" + getReasonCode() + "'" +
            ", reasonDescription='" + getReasonDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", evidenceSubmittedAt='" + getEvidenceSubmittedAt() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            "}";
    }
}
