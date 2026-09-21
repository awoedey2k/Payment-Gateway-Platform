package io.paymentgateway.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CountryPaymentMethod.
 */
@Entity
@Table(name = "country_payment_method")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CountryPaymentMethod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "min_txn_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal minTxnAmount;

    @NotNull
    @Column(name = "max_txn_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal maxTxnAmount;

    @NotNull
    @Column(name = "supports_recurring", nullable = false)
    private Boolean supportsRecurring;

    @NotNull
    @Column(name = "supports_instant_refund", nullable = false)
    private Boolean supportsInstantRefund;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    private Country country;

    @ManyToOne(optional = false)
    @NotNull
    private PaymentMethod paymentMethod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CountryPaymentMethod id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMinTxnAmount() {
        return this.minTxnAmount;
    }

    public CountryPaymentMethod minTxnAmount(BigDecimal minTxnAmount) {
        this.setMinTxnAmount(minTxnAmount);
        return this;
    }

    public void setMinTxnAmount(BigDecimal minTxnAmount) {
        this.minTxnAmount = minTxnAmount;
    }

    public BigDecimal getMaxTxnAmount() {
        return this.maxTxnAmount;
    }

    public CountryPaymentMethod maxTxnAmount(BigDecimal maxTxnAmount) {
        this.setMaxTxnAmount(maxTxnAmount);
        return this;
    }

    public void setMaxTxnAmount(BigDecimal maxTxnAmount) {
        this.maxTxnAmount = maxTxnAmount;
    }

    public Boolean getSupportsRecurring() {
        return this.supportsRecurring;
    }

    public CountryPaymentMethod supportsRecurring(Boolean supportsRecurring) {
        this.setSupportsRecurring(supportsRecurring);
        return this;
    }

    public void setSupportsRecurring(Boolean supportsRecurring) {
        this.supportsRecurring = supportsRecurring;
    }

    public Boolean getSupportsInstantRefund() {
        return this.supportsInstantRefund;
    }

    public CountryPaymentMethod supportsInstantRefund(Boolean supportsInstantRefund) {
        this.setSupportsInstantRefund(supportsInstantRefund);
        return this;
    }

    public void setSupportsInstantRefund(Boolean supportsInstantRefund) {
        this.supportsInstantRefund = supportsInstantRefund;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public CountryPaymentMethod isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CountryPaymentMethod country(Country country) {
        this.setCountry(country);
        return this;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public CountryPaymentMethod paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CountryPaymentMethod)) {
            return false;
        }
        return getId() != null && getId().equals(((CountryPaymentMethod) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CountryPaymentMethod{" +
            "id=" + getId() +
            ", minTxnAmount=" + getMinTxnAmount() +
            ", maxTxnAmount=" + getMaxTxnAmount() +
            ", supportsRecurring='" + getSupportsRecurring() + "'" +
            ", supportsInstantRefund='" + getSupportsInstantRefund() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
