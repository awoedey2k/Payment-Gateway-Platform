package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.ForexRate} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ForexRateDTO implements Serializable {

    private Long id;

    @NotNull
    private String baseCurrency;

    @NotNull
    private String quoteCurrency;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private Integer platformSpreadBps;

    @NotNull
    private Instant lockedAt;

    @NotNull
    private Instant expiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getPlatformSpreadBps() {
        return platformSpreadBps;
    }

    public void setPlatformSpreadBps(Integer platformSpreadBps) {
        this.platformSpreadBps = platformSpreadBps;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Instant lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForexRateDTO)) {
            return false;
        }

        ForexRateDTO forexRateDTO = (ForexRateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, forexRateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ForexRateDTO{" +
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
