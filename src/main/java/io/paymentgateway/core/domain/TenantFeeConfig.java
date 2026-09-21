package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.FeeBearer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TenantFeeConfig.
 */
@Entity
@Table(name = "tenant_fee_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantFeeConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fixed_fee", precision = 21, scale = 2, nullable = false)
    private BigDecimal fixedFee;

    @NotNull
    @Column(name = "percentage_fee", precision = 21, scale = 2, nullable = false)
    private BigDecimal percentageFee;

    @Column(name = "cap_amount", precision = 21, scale = 2)
    private BigDecimal capAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_bearer", nullable = false)
    private FeeBearer feeBearer;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "country", "paymentMethod" }, allowSetters = true)
    private CountryPaymentMethod countryPaymentMethod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TenantFeeConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFixedFee() {
        return this.fixedFee;
    }

    public TenantFeeConfig fixedFee(BigDecimal fixedFee) {
        this.setFixedFee(fixedFee);
        return this;
    }

    public void setFixedFee(BigDecimal fixedFee) {
        this.fixedFee = fixedFee;
    }

    public BigDecimal getPercentageFee() {
        return this.percentageFee;
    }

    public TenantFeeConfig percentageFee(BigDecimal percentageFee) {
        this.setPercentageFee(percentageFee);
        return this;
    }

    public void setPercentageFee(BigDecimal percentageFee) {
        this.percentageFee = percentageFee;
    }

    public BigDecimal getCapAmount() {
        return this.capAmount;
    }

    public TenantFeeConfig capAmount(BigDecimal capAmount) {
        this.setCapAmount(capAmount);
        return this;
    }

    public void setCapAmount(BigDecimal capAmount) {
        this.capAmount = capAmount;
    }

    public FeeBearer getFeeBearer() {
        return this.feeBearer;
    }

    public TenantFeeConfig feeBearer(FeeBearer feeBearer) {
        this.setFeeBearer(feeBearer);
        return this;
    }

    public void setFeeBearer(FeeBearer feeBearer) {
        this.feeBearer = feeBearer;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public TenantFeeConfig isActive(Boolean isActive) {
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

    public TenantFeeConfig tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    public CountryPaymentMethod getCountryPaymentMethod() {
        return this.countryPaymentMethod;
    }

    public void setCountryPaymentMethod(CountryPaymentMethod countryPaymentMethod) {
        this.countryPaymentMethod = countryPaymentMethod;
    }

    public TenantFeeConfig countryPaymentMethod(CountryPaymentMethod countryPaymentMethod) {
        this.setCountryPaymentMethod(countryPaymentMethod);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantFeeConfig)) {
            return false;
        }
        return getId() != null && getId().equals(((TenantFeeConfig) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantFeeConfig{" +
            "id=" + getId() +
            ", fixedFee=" + getFixedFee() +
            ", percentageFee=" + getPercentageFee() +
            ", capAmount=" + getCapAmount() +
            ", feeBearer='" + getFeeBearer() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
