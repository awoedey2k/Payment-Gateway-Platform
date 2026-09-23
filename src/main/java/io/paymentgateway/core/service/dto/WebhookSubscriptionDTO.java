package io.paymentgateway.core.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.WebhookSubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookSubscriptionDTO implements Serializable {

    private Long id;

    @NotNull
    private String targetUrl;

    @NotNull
    private String secretHash;

    @NotNull
    private Boolean isActive;

    @NotNull
    private CorporateTenantDTO tenant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecretHash() {
        return secretHash;
    }

    public void setSecretHash(String secretHash) {
        this.secretHash = secretHash;
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
        if (!(o instanceof WebhookSubscriptionDTO)) {
            return false;
        }

        WebhookSubscriptionDTO webhookSubscriptionDTO = (WebhookSubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, webhookSubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookSubscriptionDTO{" +
            "id=" + getId() +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secretHash='" + getSecretHash() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", tenant=" + getTenant() +
            "}";
    }
}
