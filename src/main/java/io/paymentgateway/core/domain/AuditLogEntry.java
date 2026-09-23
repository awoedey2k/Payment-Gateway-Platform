package io.paymentgateway.core.domain;

import io.paymentgateway.core.domain.enumeration.ActorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AuditLogEntry.
 */
@Entity
@Table(name = "audit_log_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogEntry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private ActorType actorType;

    @NotNull
    @Column(name = "actor_id", nullable = false)
    private String actorId;

    @NotNull
    @Column(name = "action", nullable = false)
    private String action;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "previous_hash")
    private String previousHash;

    @NotNull
    @Column(name = "entry_hash", nullable = false)
    private String entryHash;

    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuditLogEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActorType getActorType() {
        return this.actorType;
    }

    public AuditLogEntry actorType(ActorType actorType) {
        this.setActorType(actorType);
        return this;
    }

    public void setActorType(ActorType actorType) {
        this.actorType = actorType;
    }

    public String getActorId() {
        return this.actorId;
    }

    public AuditLogEntry actorId(String actorId) {
        this.setActorId(actorId);
        return this;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getAction() {
        return this.action;
    }

    public AuditLogEntry action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public AuditLogEntry entityType(String entityType) {
        this.setEntityType(entityType);
        return this;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public AuditLogEntry entityId(String entityId) {
        this.setEntityId(entityId);
        return this;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public AuditLogEntry previousHash(String previousHash) {
        this.setPreviousHash(previousHash);
        return this;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getEntryHash() {
        return this.entryHash;
    }

    public AuditLogEntry entryHash(String entryHash) {
        this.setEntryHash(entryHash);
        return this;
    }

    public void setEntryHash(String entryHash) {
        this.entryHash = entryHash;
    }

    public Instant getRecordedAt() {
        return this.recordedAt;
    }

    public AuditLogEntry recordedAt(Instant recordedAt) {
        this.setRecordedAt(recordedAt);
        return this;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLogEntry)) {
            return false;
        }
        return getId() != null && getId().equals(((AuditLogEntry) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogEntry{" +
            "id=" + getId() +
            ", actorType='" + getActorType() + "'" +
            ", actorId='" + getActorId() + "'" +
            ", action='" + getAction() + "'" +
            ", entityType='" + getEntityType() + "'" +
            ", entityId='" + getEntityId() + "'" +
            ", previousHash='" + getPreviousHash() + "'" +
            ", entryHash='" + getEntryHash() + "'" +
            ", recordedAt='" + getRecordedAt() + "'" +
            "}";
    }
}
