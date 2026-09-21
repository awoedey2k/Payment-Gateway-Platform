package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A WebhookSubscription.
 */
@Entity
@Table(name = "webhook_subscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WebhookSubscription implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @NotNull
    @Column(name = "secret_hash", nullable = false)
    private String secretHash;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenantDirectors", "apiKeys", "tenantDomains" }, allowSetters = true)
    private CorporateTenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WebhookSubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public WebhookSubscription targetUrl(String targetUrl) {
        this.setTargetUrl(targetUrl);
        return this;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecretHash() {
        return this.secretHash;
    }

    public WebhookSubscription secretHash(String secretHash) {
        this.setSecretHash(secretHash);
        return this;
    }

    public void setSecretHash(String secretHash) {
        this.secretHash = secretHash;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public WebhookSubscription isActive(Boolean isActive) {
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

    public WebhookSubscription tenant(CorporateTenant corporateTenant) {
        this.setTenant(corporateTenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebhookSubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((WebhookSubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WebhookSubscription{" +
            "id=" + getId() +
            ", targetUrl='" + getTargetUrl() + "'" +
            ", secretHash='" + getSecretHash() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
