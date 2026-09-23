package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.DisputeStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.Dispute} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.DisputeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /disputes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DisputeStatus
     */
    public static class DisputeStatusFilter extends Filter<DisputeStatus> {

        public DisputeStatusFilter() {}

        public DisputeStatusFilter(DisputeStatusFilter filter) {
            super(filter);
        }

        @Override
        public DisputeStatusFilter copy() {
            return new DisputeStatusFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter caseReference;

    private BigDecimalFilter amount;

    private StringFilter currencyCode;

    private StringFilter reasonCode;

    private StringFilter reasonDescription;

    private DisputeStatusFilter status;

    private InstantFilter dueDate;

    private InstantFilter evidenceSubmittedAt;

    private InstantFilter resolvedAt;

    private LongFilter tenantId;

    private LongFilter transactionId;

    private Boolean distinct;

    public DisputeCriteria() {}

    public DisputeCriteria(DisputeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.caseReference = other.optionalCaseReference().map(StringFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(BigDecimalFilter::copy).orElse(null);
        this.currencyCode = other.optionalCurrencyCode().map(StringFilter::copy).orElse(null);
        this.reasonCode = other.optionalReasonCode().map(StringFilter::copy).orElse(null);
        this.reasonDescription = other.optionalReasonDescription().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DisputeStatusFilter::copy).orElse(null);
        this.dueDate = other.optionalDueDate().map(InstantFilter::copy).orElse(null);
        this.evidenceSubmittedAt = other.optionalEvidenceSubmittedAt().map(InstantFilter::copy).orElse(null);
        this.resolvedAt = other.optionalResolvedAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DisputeCriteria copy() {
        return new DisputeCriteria(this);
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

    public StringFilter getCaseReference() {
        return caseReference;
    }

    public Optional<StringFilter> optionalCaseReference() {
        return Optional.ofNullable(caseReference);
    }

    public StringFilter caseReference() {
        if (caseReference == null) {
            setCaseReference(new StringFilter());
        }
        return caseReference;
    }

    public void setCaseReference(StringFilter caseReference) {
        this.caseReference = caseReference;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public Optional<BigDecimalFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            setAmount(new BigDecimalFilter());
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public StringFilter getCurrencyCode() {
        return currencyCode;
    }

    public Optional<StringFilter> optionalCurrencyCode() {
        return Optional.ofNullable(currencyCode);
    }

    public StringFilter currencyCode() {
        if (currencyCode == null) {
            setCurrencyCode(new StringFilter());
        }
        return currencyCode;
    }

    public void setCurrencyCode(StringFilter currencyCode) {
        this.currencyCode = currencyCode;
    }

    public StringFilter getReasonCode() {
        return reasonCode;
    }

    public Optional<StringFilter> optionalReasonCode() {
        return Optional.ofNullable(reasonCode);
    }

    public StringFilter reasonCode() {
        if (reasonCode == null) {
            setReasonCode(new StringFilter());
        }
        return reasonCode;
    }

    public void setReasonCode(StringFilter reasonCode) {
        this.reasonCode = reasonCode;
    }

    public StringFilter getReasonDescription() {
        return reasonDescription;
    }

    public Optional<StringFilter> optionalReasonDescription() {
        return Optional.ofNullable(reasonDescription);
    }

    public StringFilter reasonDescription() {
        if (reasonDescription == null) {
            setReasonDescription(new StringFilter());
        }
        return reasonDescription;
    }

    public void setReasonDescription(StringFilter reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public DisputeStatusFilter getStatus() {
        return status;
    }

    public Optional<DisputeStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DisputeStatusFilter status() {
        if (status == null) {
            setStatus(new DisputeStatusFilter());
        }
        return status;
    }

    public void setStatus(DisputeStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getDueDate() {
        return dueDate;
    }

    public Optional<InstantFilter> optionalDueDate() {
        return Optional.ofNullable(dueDate);
    }

    public InstantFilter dueDate() {
        if (dueDate == null) {
            setDueDate(new InstantFilter());
        }
        return dueDate;
    }

    public void setDueDate(InstantFilter dueDate) {
        this.dueDate = dueDate;
    }

    public InstantFilter getEvidenceSubmittedAt() {
        return evidenceSubmittedAt;
    }

    public Optional<InstantFilter> optionalEvidenceSubmittedAt() {
        return Optional.ofNullable(evidenceSubmittedAt);
    }

    public InstantFilter evidenceSubmittedAt() {
        if (evidenceSubmittedAt == null) {
            setEvidenceSubmittedAt(new InstantFilter());
        }
        return evidenceSubmittedAt;
    }

    public void setEvidenceSubmittedAt(InstantFilter evidenceSubmittedAt) {
        this.evidenceSubmittedAt = evidenceSubmittedAt;
    }

    public InstantFilter getResolvedAt() {
        return resolvedAt;
    }

    public Optional<InstantFilter> optionalResolvedAt() {
        return Optional.ofNullable(resolvedAt);
    }

    public InstantFilter resolvedAt() {
        if (resolvedAt == null) {
            setResolvedAt(new InstantFilter());
        }
        return resolvedAt;
    }

    public void setResolvedAt(InstantFilter resolvedAt) {
        this.resolvedAt = resolvedAt;
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

    public LongFilter getTransactionId() {
        return transactionId;
    }

    public Optional<LongFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public LongFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new LongFilter());
        }
        return transactionId;
    }

    public void setTransactionId(LongFilter transactionId) {
        this.transactionId = transactionId;
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
        final DisputeCriteria that = (DisputeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(caseReference, that.caseReference) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(currencyCode, that.currencyCode) &&
            Objects.equals(reasonCode, that.reasonCode) &&
            Objects.equals(reasonDescription, that.reasonDescription) &&
            Objects.equals(status, that.status) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(evidenceSubmittedAt, that.evidenceSubmittedAt) &&
            Objects.equals(resolvedAt, that.resolvedAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            caseReference,
            amount,
            currencyCode,
            reasonCode,
            reasonDescription,
            status,
            dueDate,
            evidenceSubmittedAt,
            resolvedAt,
            tenantId,
            transactionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCaseReference().map(f -> "caseReference=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalCurrencyCode().map(f -> "currencyCode=" + f + ", ").orElse("") +
            optionalReasonCode().map(f -> "reasonCode=" + f + ", ").orElse("") +
            optionalReasonDescription().map(f -> "reasonDescription=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalDueDate().map(f -> "dueDate=" + f + ", ").orElse("") +
            optionalEvidenceSubmittedAt().map(f -> "evidenceSubmittedAt=" + f + ", ").orElse("") +
            optionalResolvedAt().map(f -> "resolvedAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
