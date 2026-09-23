package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.PayoutFrequency;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PayoutSchedule.
 */
@Entity
@Table(name = "payout_schedule")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PayoutSchedule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_mode", nullable = false)
    private PayoutFrequency frequencyMode;

    @NotNull
    @Column(name = "threshold_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal thresholdAmount;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PayoutSchedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayoutFrequency getFrequencyMode() {
        return this.frequencyMode;
    }

    public PayoutSchedule frequencyMode(PayoutFrequency frequencyMode) {
        this.setFrequencyMode(frequencyMode);
        return this;
    }

    public void setFrequencyMode(PayoutFrequency frequencyMode) {
        this.frequencyMode = frequencyMode;
    }

    public BigDecimal getThresholdAmount() {
        return this.thresholdAmount;
    }

    public PayoutSchedule thresholdAmount(BigDecimal thresholdAmount) {
        this.setThresholdAmount(thresholdAmount);
        return this;
    }

    public void setThresholdAmount(BigDecimal thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public PayoutSchedule isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public PayoutSchedule tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayoutSchedule)) {
            return false;
        }
        return getId() != null && getId().equals(((PayoutSchedule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PayoutSchedule{" +
            "id=" + getId() +
            ", frequencyMode='" + getFrequencyMode() + "'" +
            ", thresholdAmount=" + getThresholdAmount() +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
