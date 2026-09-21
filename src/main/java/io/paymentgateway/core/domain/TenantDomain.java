package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TenantDomain.
 */
@Entity
@Table(name = "tenant_domain")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "custom_domain", nullable = false, unique = true)
    private String customDomain;

    @Column(name = "supported_locales")
    private String supportedLocales;

    @NotNull
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TenantDomain id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomDomain() {
        return this.customDomain;
    }

    public TenantDomain customDomain(String customDomain) {
        this.setCustomDomain(customDomain);
        return this;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }

    public String getSupportedLocales() {
        return this.supportedLocales;
    }

    public TenantDomain supportedLocales(String supportedLocales) {
        this.setSupportedLocales(supportedLocales);
        return this;
    }

    public void setSupportedLocales(String supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public TenantDomain isVerified(Boolean isVerified) {
        this.setIsVerified(isVerified);
        return this;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public CorporateTenant getTenant() {
        return this.tenant;
    }

    public void setTenant(CorporateTenant corporateTenant) {
        this.tenant = corporateTenant;
    }

    public TenantDomain tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenantDomain)) {
            return false;
        }
        return getId() != null && getId().equals(((TenantDomain) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDomain{" +
            "id=" + getId() +
            ", customDomain='" + getCustomDomain() + "'" +
            ", supportedLocales='" + getSupportedLocales() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            "}";
    }
}
