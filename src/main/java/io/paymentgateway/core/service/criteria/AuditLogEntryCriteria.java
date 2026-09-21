package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.ActorType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.AuditLogEntry} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.AuditLogEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /audit-log-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuditLogEntryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ActorType
     */
    public static class ActorTypeFilter extends Filter<ActorType> {

        public ActorTypeFilter() {}

        public ActorTypeFilter(ActorTypeFilter filter) {
            super(filter);
        }

        @Override
        public ActorTypeFilter copy() {
            return new ActorTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ActorTypeFilter actorType;

    private StringFilter actorId;

    private StringFilter action;

    private StringFilter entityType;

    private StringFilter entityId;

    private StringFilter previousHash;

    private StringFilter entryHash;

    private InstantFilter recordedAt;

    private Boolean distinct;

    public AuditLogEntryCriteria() {}

    public AuditLogEntryCriteria(AuditLogEntryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.actorType = other.optionalActorType().map(ActorTypeFilter::copy).orElse(null);
        this.actorId = other.optionalActorId().map(StringFilter::copy).orElse(null);
        this.action = other.optionalAction().map(StringFilter::copy).orElse(null);
        this.entityType = other.optionalEntityType().map(StringFilter::copy).orElse(null);
        this.entityId = other.optionalEntityId().map(StringFilter::copy).orElse(null);
        this.previousHash = other.optionalPreviousHash().map(StringFilter::copy).orElse(null);
        this.entryHash = other.optionalEntryHash().map(StringFilter::copy).orElse(null);
        this.recordedAt = other.optionalRecordedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AuditLogEntryCriteria copy() {
        return new AuditLogEntryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ActorTypeFilter getActorType() {
        return actorType;
    }

    public Optional<ActorTypeFilter> optionalActorType() {
        return Optional.ofNullable(actorType);
    }

    public ActorTypeFilter actorType() {
        if (actorType == null) {
            setActorType(new ActorTypeFilter());
        }
        return actorType;
    }

    public void setActorType(ActorTypeFilter actorType) {
        this.actorType = actorType;
    }

    public StringFilter getActorId() {
        return actorId;
    }

    public Optional<StringFilter> optionalActorId() {
        return Optional.ofNullable(actorId);
    }

    public StringFilter actorId() {
        if (actorId == null) {
            setActorId(new StringFilter());
        }
        return actorId;
    }

    public void setActorId(StringFilter actorId) {
        this.actorId = actorId;
    }

    public StringFilter getAction() {
        return action;
    }

    public Optional<StringFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public StringFilter action() {
        if (action == null) {
            setAction(new StringFilter());
        }
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
    }

    public StringFilter getEntityType() {
        return entityType;
    }

    public Optional<StringFilter> optionalEntityType() {
        return Optional.ofNullable(entityType);
    }

    public StringFilter entityType() {
        if (entityType == null) {
            setEntityType(new StringFilter());
        }
        return entityType;
    }

    public void setEntityType(StringFilter entityType) {
        this.entityType = entityType;
    }

    public StringFilter getEntityId() {
        return entityId;
    }

    public Optional<StringFilter> optionalEntityId() {
        return Optional.ofNullable(entityId);
    }

    public StringFilter entityId() {
        if (entityId == null) {
            setEntityId(new StringFilter());
        }
        return entityId;
    }

    public void setEntityId(StringFilter entityId) {
        this.entityId = entityId;
    }

    public StringFilter getPreviousHash() {
        return previousHash;
    }

    public Optional<StringFilter> optionalPreviousHash() {
        return Optional.ofNullable(previousHash);
    }

    public StringFilter previousHash() {
        if (previousHash == null) {
            setPreviousHash(new StringFilter());
        }
        return previousHash;
    }

    public void setPreviousHash(StringFilter previousHash) {
        this.previousHash = previousHash;
    }

    public StringFilter getEntryHash() {
        return entryHash;
    }

    public Optional<StringFilter> optionalEntryHash() {
        return Optional.ofNullable(entryHash);
    }

    public StringFilter entryHash() {
        if (entryHash == null) {
            setEntryHash(new StringFilter());
        }
        return entryHash;
    }

    public void setEntryHash(StringFilter entryHash) {
        this.entryHash = entryHash;
    }

    public InstantFilter getRecordedAt() {
        return recordedAt;
    }

    public Optional<InstantFilter> optionalRecordedAt() {
        return Optional.ofNullable(recordedAt);
    }

    public InstantFilter recordedAt() {
        if (recordedAt == null) {
            setRecordedAt(new InstantFilter());
        }
        return recordedAt;
    }

    public void setRecordedAt(InstantFilter recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuditLogEntryCriteria that = (AuditLogEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(actorType, that.actorType) &&
            Objects.equals(actorId, that.actorId) &&
            Objects.equals(action, that.action) &&
            Objects.equals(entityType, that.entityType) &&
            Objects.equals(entityId, that.entityId) &&
            Objects.equals(previousHash, that.previousHash) &&
            Objects.equals(entryHash, that.entryHash) &&
            Objects.equals(recordedAt, that.recordedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actorType, actorId, action, entityType, entityId, previousHash, entryHash, recordedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuditLogEntryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalActorType().map(f -> "actorType=" + f + ", ").orElse("") +
            optionalActorId().map(f -> "actorId=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalEntityType().map(f -> "entityType=" + f + ", ").orElse("") +
            optionalEntityId().map(f -> "entityId=" + f + ", ").orElse("") +
            optionalPreviousHash().map(f -> "previousHash=" + f + ", ").orElse("") +
            optionalEntryHash().map(f -> "entryHash=" + f + ", ").orElse("") +
            optionalRecordedAt().map(f -> "recordedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
