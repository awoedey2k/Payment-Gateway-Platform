package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.RoutingRuleScope;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.RoutingRule} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoutingRuleDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer priority;

    @NotNull
    private RoutingRuleScope scope;

    private String countryCode;

    private String currencyCode;

    private String cardBrand;

    @NotNull
    private String primaryAdapter;

    private String fallbackAdapter;

    @NotNull
    private Integer maxRetries;

    @NotNull
    private Boolean isActive;

    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public RoutingRuleScope getScope() {
        return scope;
    }

    public void setScope(RoutingRuleScope scope) {
        this.scope = scope;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getPrimaryAdapter() {
        return primaryAdapter;
    }

    public void setPrimaryAdapter(String primaryAdapter) {
        this.primaryAdapter = primaryAdapter;
    }

    public String getFallbackAdapter() {
        return fallbackAdapter;
    }

    public void setFallbackAdapter(String fallbackAdapter) {
        this.fallbackAdapter = fallbackAdapter;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public CorporateTenantDTO getTenant() {
        return tenant;
    }

    public void setTenant(CorporateTenantDTO tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoutingRuleDTO)) {
            return false;
        }

        RoutingRuleDTO routingRuleDTO = (RoutingRuleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, routingRuleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoutingRuleDTO{" +
            "id=" + getId() +
            ", priority=" + getPriority() +
            ", scope='" + getScope() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", cardBrand='" + getCardBrand() + "'" +
            ", primaryAdapter='" + getPrimaryAdapter() + "'" +
            ", fallbackAdapter='" + getFallbackAdapter() + "'" +
            ", maxRetries=" + getMaxRetries() +
            ", isActive='" + getIsActive() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
