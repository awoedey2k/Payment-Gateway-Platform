package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.ActorType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.AuditLogEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogEntryDTO implements Serializable {

    private Long id;

    @NotNull
    private ActorType actorType;

    @NotNull
    private String actorId;

    @NotNull
    private String action;

    @NotNull
    private String entityType;

    @NotNull
    private String entityId;

    private String previousHash;

    @NotNull
    private String entryHash;

    @NotNull
    private Instant recordedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public void setActorType(ActorType actorType) {
        this.actorType = actorType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getEntryHash() {
        return entryHash;
    }

    public void setEntryHash(String entryHash) {
        this.entryHash = entryHash;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLogEntryDTO)) {
            return false;
        }

        AuditLogEntryDTO auditLogEntryDTO = (AuditLogEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, auditLogEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogEntryDTO{" +
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
