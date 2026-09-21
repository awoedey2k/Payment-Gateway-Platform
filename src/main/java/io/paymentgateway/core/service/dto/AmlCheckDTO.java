package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.AmlDecision;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.AmlCheck} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AmlCheckDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer riskScore;

    @NotNull
    private AmlDecision decision;

    private String ruleTriggered;

    @NotNull
    private Instant checkedAt;

    private CorporateTenantDTO tenant;

    private TransactionDTO transaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public AmlDecision getDecision() {
        return decision;
    }

    public void setDecision(AmlDecision decision) {
        this.decision = decision;
    }

    public String getRuleTriggered() {
        return ruleTriggered;
    }

    public void setRuleTriggered(String ruleTriggered) {
        this.ruleTriggered = ruleTriggered;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmlCheckDTO)) {
            return false;
        }

        AmlCheckDTO amlCheckDTO = (AmlCheckDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, amlCheckDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmlCheckDTO{" +
            "id=" + getId() +
            ", riskScore=" + getRiskScore() +
            ", decision='" + getDecision() + "'" +
            ", ruleTriggered='" + getRuleTriggered() + "'" +
            ", checkedAt='" + getCheckedAt() + "'" +
            ", tenant=" + getTenant() +
            ", transaction=" + getTransaction() +
            "}";
    }
}
