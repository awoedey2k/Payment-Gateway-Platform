package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.TenantDomain} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TenantDomainDTO implements Serializable {

    private Long id;

    @NotNull
    private String customDomain;

    private String supportedLocales;

    private String defaultLocale;

    private String fallbackLocale;

    @NotNull
    private Boolean isVerified;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomDomain() {
        return customDomain;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }

    public String getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(String supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getFallbackLocale() {
        return fallbackLocale;
    }

    public void setFallbackLocale(String fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
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
        if (!(o instanceof TenantDomainDTO)) {
            return false;
        }

        TenantDomainDTO tenantDomainDTO = (TenantDomainDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tenantDomainDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TenantDomainDTO{" +
            "id=" + getId() +
            ", customDomain='" + getCustomDomain() + "'" +
            ", supportedLocales='" + getSupportedLocales() + "'" +
            ", defaultLocale='" + getDefaultLocale() + "'" +
            ", fallbackLocale='" + getFallbackLocale() + "'" +
            ", isVerified='" + getIsVerified() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
