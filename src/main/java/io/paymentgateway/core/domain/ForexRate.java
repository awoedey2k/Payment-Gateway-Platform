package io.paymentgateway.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ForexRate.
 */
@Entity
@Table(name = "forex_rate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ForexRate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "base_currency", nullable = false)
    private String baseCurrency;

    @NotNull
    @Column(name = "quote_currency", nullable = false)
    private String quoteCurrency;

    @NotNull
    @Column(name = "rate", precision = 21, scale = 2, nullable = false)
    private BigDecimal rate;

    @NotNull
    @Column(name = "platform_spread_bps", nullable = false)
    private Integer platformSpreadBps;

    @NotNull
    @Column(name = "locked_at", nullable = false)
    private Instant lockedAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ForexRate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return this.baseCurrency;
    }

    public ForexRate baseCurrency(String baseCurrency) {
        this.setBaseCurrency(baseCurrency);
        return this;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return this.quoteCurrency;
    }

    public ForexRate quoteCurrency(String quoteCurrency) {
        this.setQuoteCurrency(quoteCurrency);
        return this;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimal getRate() {
        return this.rate;
    }

    public ForexRate rate(BigDecimal rate) {
        this.setRate(rate);
        return this;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getPlatformSpreadBps() {
        return this.platformSpreadBps;
    }

    public ForexRate platformSpreadBps(Integer platformSpreadBps) {
        this.setPlatformSpreadBps(platformSpreadBps);
        return this;
    }

    public void setPlatformSpreadBps(Integer platformSpreadBps) {
        this.platformSpreadBps = platformSpreadBps;
    }

    public Instant getLockedAt() {
        return this.lockedAt;
    }

    public ForexRate lockedAt(Instant lockedAt) {
        this.setLockedAt(lockedAt);
        return this;
    }

    public void setLockedAt(Instant lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public ForexRate expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForexRate)) {
            return false;
        }
        return getId() != null && getId().equals(((ForexRate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ForexRate{" +
            "id=" + getId() +
            ", baseCurrency='" + getBaseCurrency() + "'" +
            ", quoteCurrency='" + getQuoteCurrency() + "'" +
            ", rate=" + getRate() +
            ", platformSpreadBps=" + getPlatformSpreadBps() +
            ", lockedAt='" + getLockedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
