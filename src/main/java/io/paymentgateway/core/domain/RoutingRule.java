package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.RoutingRuleScope;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RoutingRule.
 */
@Entity
@Table(name = "routing_rule")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoutingRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "priority", nullable = false)
    private Integer priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private RoutingRuleScope scope;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "card_brand")
    private String cardBrand;

    @NotNull
    @Column(name = "primary_adapter", nullable = false)
    private String primaryAdapter;

    @Column(name = "fallback_adapter")
    private String fallbackAdapter;

    @NotNull
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RoutingRule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public RoutingRule priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public RoutingRuleScope getScope() {
        return this.scope;
    }

    public RoutingRule scope(RoutingRuleScope scope) {
        this.setScope(scope);
        return this;
    }

    public void setScope(RoutingRuleScope scope) {
        this.scope = scope;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public RoutingRule countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public RoutingRule currencyCode(String currencyCode) {
        this.setCurrencyCode(currencyCode);
        return this;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCardBrand() {
        return this.cardBrand;
    }

    public RoutingRule cardBrand(String cardBrand) {
        this.setCardBrand(cardBrand);
        return this;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getPrimaryAdapter() {
        return this.primaryAdapter;
    }

    public RoutingRule primaryAdapter(String primaryAdapter) {
        this.setPrimaryAdapter(primaryAdapter);
        return this;
    }

    public void setPrimaryAdapter(String primaryAdapter) {
        this.primaryAdapter = primaryAdapter;
    }

    public String getFallbackAdapter() {
        return this.fallbackAdapter;
    }

    public RoutingRule fallbackAdapter(String fallbackAdapter) {
        this.setFallbackAdapter(fallbackAdapter);
        return this;
    }

    public void setFallbackAdapter(String fallbackAdapter) {
        this.fallbackAdapter = fallbackAdapter;
    }

    public Integer getMaxRetries() {
        return this.maxRetries;
    }

    public RoutingRule maxRetries(Integer maxRetries) {
        this.setMaxRetries(maxRetries);
        return this;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public RoutingRule isActive(Boolean isActive) {
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

    public RoutingRule tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoutingRule)) {
            return false;
        }
        return getId() != null && getId().equals(((RoutingRule) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoutingRule{" +
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
            "}";
    }
}
