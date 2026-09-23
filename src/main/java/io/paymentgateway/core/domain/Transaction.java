package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

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

    @Column(name = "tenant_reference")
    private String tenantReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "fee_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal feeAmount;

    @NotNull
    @Column(name = "net_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal netAmount;

    @NotNull
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @NotNull
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @NotNull
    @Column(name = "payment_method_code", nullable = false)
    private String paymentMethodCode;

    @NotNull
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "transaction" }, allowSetters = true)
    private Set<Refund> refunds = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Transaction reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTenantReference() {
        return this.tenantReference;
    }

    public Transaction tenantReference(String tenantReference) {
        this.setTenantReference(tenantReference);
        return this;
    }

    public void setTenantReference(String tenantReference) {
        this.tenantReference = tenantReference;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transaction status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public Transaction feeAmount(BigDecimal feeAmount) {
        this.setFeeAmount(feeAmount);
        return this;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getNetAmount() {
        return this.netAmount;
    }

    public Transaction netAmount(BigDecimal netAmount) {
        this.setNetAmount(netAmount);
        return this;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Transaction currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Transaction countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPaymentMethodCode() {
        return this.paymentMethodCode;
    }

    public Transaction paymentMethodCode(String paymentMethodCode) {
        this.setPaymentMethodCode(paymentMethodCode);
        return this;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public Transaction idempotencyKey(String idempotencyKey) {
        this.setIdempotencyKey(idempotencyKey);
        return this;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public Transaction customerEmail(String customerEmail) {
        this.setCustomerEmail(customerEmail);
        return this;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return this.customerPhone;
    }

    public Transaction customerPhone(String customerPhone) {
        this.setCustomerPhone(customerPhone);
        return this;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Transaction createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public Transaction completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Set<Refund> getRefunds() {
        return this.refunds;
    }

    public void setRefunds(Set<Refund> refunds) {
        if (this.refunds != null) {
            this.refunds.forEach(i -> i.setTransaction(null));
        }
        if (refunds != null) {
            refunds.forEach(i -> i.setTransaction(this));
        }
        this.refunds = refunds;
    }

    public Transaction refunds(Set<Refund> refunds) {
        this.setRefunds(refunds);
        return this;
    }

    public Transaction addRefund(Refund refund) {
        this.refunds.add(refund);
        refund.setTransaction(this);
        return this;
    }

    public Transaction removeRefund(Refund refund) {
        this.refunds.remove(refund);
        refund.setTransaction(null);
        return this;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public Transaction tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", tenantReference='" + getTenantReference() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", feeAmount=" + getFeeAmount() +
            ", netAmount=" + getNetAmount() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", paymentMethodCode='" + getPaymentMethodCode() + "'" +
            ", idempotencyKey='" + getIdempotencyKey() + "'" +
            ", customerEmail='" + getCustomerEmail() + "'" +
            ", customerPhone='" + getCustomerPhone() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            "}";
    }
}
