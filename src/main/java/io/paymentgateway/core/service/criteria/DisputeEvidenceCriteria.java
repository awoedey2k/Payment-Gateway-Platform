package io.paymentgateway.core.service.criteria;

import io.paymentgateway.core.domain.enumeration.DisputeEvidenceType;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.paymentgateway.core.domain.DisputeEvidence} entity. This class is used
 * in {@link io.paymentgateway.core.web.rest.DisputeEvidenceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /dispute-evidences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeEvidenceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DisputeEvidenceType
     */
    public static class DisputeEvidenceTypeFilter extends Filter<DisputeEvidenceType> {

        public DisputeEvidenceTypeFilter() {}

        public DisputeEvidenceTypeFilter(DisputeEvidenceTypeFilter filter) {
            super(filter);
        }

        @Override
        public DisputeEvidenceTypeFilter copy() {
            return new DisputeEvidenceTypeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DisputeEvidenceTypeFilter evidenceType;

    private StringFilter fileName;

    private StringFilter fileUrl;

    private LongFilter fileSizeBytes;

    private StringFilter mimeType;

    private StringFilter sha256Checksum;

    private InstantFilter uploadedAt;

    private LongFilter disputeId;

    private Boolean distinct;

    public DisputeEvidenceCriteria() {}

    public DisputeEvidenceCriteria(DisputeEvidenceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.evidenceType = other.optionalEvidenceType().map(DisputeEvidenceTypeFilter::copy).orElse(null);
        this.fileName = other.optionalFileName().map(StringFilter::copy).orElse(null);
        this.fileUrl = other.optionalFileUrl().map(StringFilter::copy).orElse(null);
        this.fileSizeBytes = other.optionalFileSizeBytes().map(LongFilter::copy).orElse(null);
        this.mimeType = other.optionalMimeType().map(StringFilter::copy).orElse(null);
        this.sha256Checksum = other.optionalSha256Checksum().map(StringFilter::copy).orElse(null);
        this.uploadedAt = other.optionalUploadedAt().map(InstantFilter::copy).orElse(null);
        this.disputeId = other.optionalDisputeId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DisputeEvidenceCriteria copy() {
        return new DisputeEvidenceCriteria(this);
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

    public DisputeEvidenceTypeFilter getEvidenceType() {
        return evidenceType;
    }

    public Optional<DisputeEvidenceTypeFilter> optionalEvidenceType() {
        return Optional.ofNullable(evidenceType);
    }

    public DisputeEvidenceTypeFilter evidenceType() {
        if (evidenceType == null) {
            setEvidenceType(new DisputeEvidenceTypeFilter());
        }
        return evidenceType;
    }

    public void setEvidenceType(DisputeEvidenceTypeFilter evidenceType) {
        this.evidenceType = evidenceType;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public Optional<StringFilter> optionalFileName() {
        return Optional.ofNullable(fileName);
    }

    public StringFilter fileName() {
        if (fileName == null) {
            setFileName(new StringFilter());
        }
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public StringFilter getFileUrl() {
        return fileUrl;
    }

    public Optional<StringFilter> optionalFileUrl() {
        return Optional.ofNullable(fileUrl);
    }

    public StringFilter fileUrl() {
        if (fileUrl == null) {
            setFileUrl(new StringFilter());
        }
        return fileUrl;
    }

    public void setFileUrl(StringFilter fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LongFilter getFileSizeBytes() {
        return fileSizeBytes;
    }

    public Optional<LongFilter> optionalFileSizeBytes() {
        return Optional.ofNullable(fileSizeBytes);
    }

    public LongFilter fileSizeBytes() {
        if (fileSizeBytes == null) {
            setFileSizeBytes(new LongFilter());
        }
        return fileSizeBytes;
    }

    public void setFileSizeBytes(LongFilter fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public StringFilter getMimeType() {
        return mimeType;
    }

    public Optional<StringFilter> optionalMimeType() {
        return Optional.ofNullable(mimeType);
    }

    public StringFilter mimeType() {
        if (mimeType == null) {
            setMimeType(new StringFilter());
        }
        return mimeType;
    }

    public void setMimeType(StringFilter mimeType) {
        this.mimeType = mimeType;
    }

    public StringFilter getSha256Checksum() {
        return sha256Checksum;
    }

    public Optional<StringFilter> optionalSha256Checksum() {
        return Optional.ofNullable(sha256Checksum);
    }

    public StringFilter sha256Checksum() {
        if (sha256Checksum == null) {
            setSha256Checksum(new StringFilter());
        }
        return sha256Checksum;
    }

    public void setSha256Checksum(StringFilter sha256Checksum) {
        this.sha256Checksum = sha256Checksum;
    }

    public InstantFilter getUploadedAt() {
        return uploadedAt;
    }

    public Optional<InstantFilter> optionalUploadedAt() {
        return Optional.ofNullable(uploadedAt);
    }

    public InstantFilter uploadedAt() {
        if (uploadedAt == null) {
            setUploadedAt(new InstantFilter());
        }
        return uploadedAt;
    }

    public void setUploadedAt(InstantFilter uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LongFilter getDisputeId() {
        return disputeId;
    }

    public Optional<LongFilter> optionalDisputeId() {
        return Optional.ofNullable(disputeId);
    }

    public LongFilter disputeId() {
        if (disputeId == null) {
            setDisputeId(new LongFilter());
        }
        return disputeId;
    }

    public void setDisputeId(LongFilter disputeId) {
        this.disputeId = disputeId;
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
        final DisputeEvidenceCriteria that = (DisputeEvidenceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(evidenceType, that.evidenceType) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(fileUrl, that.fileUrl) &&
            Objects.equals(fileSizeBytes, that.fileSizeBytes) &&
            Objects.equals(mimeType, that.mimeType) &&
            Objects.equals(sha256Checksum, that.sha256Checksum) &&
            Objects.equals(uploadedAt, that.uploadedAt) &&
            Objects.equals(disputeId, that.disputeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, evidenceType, fileName, fileUrl, fileSizeBytes, mimeType, sha256Checksum, uploadedAt, disputeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeEvidenceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEvidenceType().map(f -> "evidenceType=" + f + ", ").orElse("") +
            optionalFileName().map(f -> "fileName=" + f + ", ").orElse("") +
            optionalFileUrl().map(f -> "fileUrl=" + f + ", ").orElse("") +
            optionalFileSizeBytes().map(f -> "fileSizeBytes=" + f + ", ").orElse("") +
            optionalMimeType().map(f -> "mimeType=" + f + ", ").orElse("") +
            optionalSha256Checksum().map(f -> "sha256Checksum=" + f + ", ").orElse("") +
            optionalUploadedAt().map(f -> "uploadedAt=" + f + ", ").orElse("") +
            optionalDisputeId().map(f -> "disputeId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
