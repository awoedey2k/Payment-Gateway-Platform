package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.AmlDecision;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.AmlCheck} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.AmlCheckResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /aml-checks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AmlCheckCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AmlDecision
     */
    public static class AmlDecisionFilter extends Filter<AmlDecision> {

        public AmlDecisionFilter() {}

        public AmlDecisionFilter(AmlDecisionFilter filter) {
            super(filter);
        }

        @Override
        public AmlDecisionFilter copy() {
            return new AmlDecisionFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter riskScore;

    private AmlDecisionFilter decision;

    private StringFilter ruleTriggered;

    private InstantFilter checkedAt;

    private LongFilter tenantId;

    private LongFilter transactionId;

    private Boolean distinct;

    public AmlCheckCriteria() {}

    public AmlCheckCriteria(AmlCheckCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.riskScore = other.optionalRiskScore().map(IntegerFilter::copy).orElse(null);
        this.decision = other.optionalDecision().map(AmlDecisionFilter::copy).orElse(null);
        this.ruleTriggered = other.optionalRuleTriggered().map(StringFilter::copy).orElse(null);
        this.checkedAt = other.optionalCheckedAt().map(InstantFilter::copy).orElse(null);
        this.tenantId = other.optionalTenantId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AmlCheckCriteria copy() {
        return new AmlCheckCriteria(this);
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

    public IntegerFilter getRiskScore() {
        return riskScore;
    }

    public Optional<IntegerFilter> optionalRiskScore() {
        return Optional.ofNullable(riskScore);
    }

    public IntegerFilter riskScore() {
        if (riskScore == null) {
            setRiskScore(new IntegerFilter());
        }
        return riskScore;
    }

    public void setRiskScore(IntegerFilter riskScore) {
        this.riskScore = riskScore;
    }

    public AmlDecisionFilter getDecision() {
        return decision;
    }

    public Optional<AmlDecisionFilter> optionalDecision() {
        return Optional.ofNullable(decision);
    }

    public AmlDecisionFilter decision() {
        if (decision == null) {
            setDecision(new AmlDecisionFilter());
        }
        return decision;
    }

    public void setDecision(AmlDecisionFilter decision) {
        this.decision = decision;
    }

    public StringFilter getRuleTriggered() {
        return ruleTriggered;
    }

    public Optional<StringFilter> optionalRuleTriggered() {
        return Optional.ofNullable(ruleTriggered);
    }

    public StringFilter ruleTriggered() {
        if (ruleTriggered == null) {
            setRuleTriggered(new StringFilter());
        }
        return ruleTriggered;
    }

    public void setRuleTriggered(StringFilter ruleTriggered) {
        this.ruleTriggered = ruleTriggered;
    }

    public InstantFilter getCheckedAt() {
        return checkedAt;
    }

    public Optional<InstantFilter> optionalCheckedAt() {
        return Optional.ofNullable(checkedAt);
    }

    public InstantFilter checkedAt() {
        if (checkedAt == null) {
            setCheckedAt(new InstantFilter());
        }
        return checkedAt;
    }

    public void setCheckedAt(InstantFilter checkedAt) {
        this.checkedAt = checkedAt;
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
        final AmlCheckCriteria that = (AmlCheckCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(riskScore, that.riskScore) &&
            Objects.equals(decision, that.decision) &&
            Objects.equals(ruleTriggered, that.ruleTriggered) &&
            Objects.equals(checkedAt, that.checkedAt) &&
            Objects.equals(tenantId, that.tenantId) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, riskScore, decision, ruleTriggered, checkedAt, tenantId, transactionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmlCheckCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRiskScore().map(f -> "riskScore=" + f + ", ").orElse("") +
            optionalDecision().map(f -> "decision=" + f + ", ").orElse("") +
            optionalRuleTriggered().map(f -> "ruleTriggered=" + f + ", ").orElse("") +
            optionalCheckedAt().map(f -> "checkedAt=" + f + ", ").orElse("") +
            optionalTenantId().map(f -> "tenantId=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
