package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.AmlDecision;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AmlCheck.
 */
@Entity
@Table(name = "aml_check")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AmlCheck implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private AmlDecision decision;

    @Column(name = "rule_triggered")
    private String ruleTriggered;

    @NotNull
    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "refunds", "tenant" }, allowSetters = true)
    private Transaction transaction;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AmlCheck id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRiskScore() {
        return this.riskScore;
    }

    public AmlCheck riskScore(Integer riskScore) {
        this.setRiskScore(riskScore);
        return this;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public AmlDecision getDecision() {
        return this.decision;
    }

    public AmlCheck decision(AmlDecision decision) {
        this.setDecision(decision);
        return this;
    }

    public void setDecision(AmlDecision decision) {
        this.decision = decision;
    }

    public String getRuleTriggered() {
        return this.ruleTriggered;
    }

    public AmlCheck ruleTriggered(String ruleTriggered) {
        this.setRuleTriggered(ruleTriggered);
        return this;
    }

    public void setRuleTriggered(String ruleTriggered) {
        this.ruleTriggered = ruleTriggered;
    }

    public Instant getCheckedAt() {
        return this.checkedAt;
    }

    public AmlCheck checkedAt(Instant checkedAt) {
        this.setCheckedAt(checkedAt);
        return this;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public AmlCheck tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public AmlCheck transaction(Transaction transaction) {
        this.setTransaction(transaction);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmlCheck)) {
            return false;
        }
        return getId() != null && getId().equals(((AmlCheck) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmlCheck{" +
            "id=" + getId() +
            ", riskScore=" + getRiskScore() +
            ", decision='" + getDecision() + "'" +
            ", ruleTriggered='" + getRuleTriggered() + "'" +
            ", checkedAt='" + getCheckedAt() + "'" +
            "}";
    }
}
