package io.paymentgateway.core.service.dto;

import io.paymentgateway.core.domain.enumeration.DisputeEvidenceType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link io.paymentgateway.core.domain.DisputeEvidence} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeEvidenceDTO implements Serializable {

    private Long id;

    @NotNull
    private DisputeEvidenceType evidenceType;

    @NotNull
    private String fileName;

    @NotNull
    private String fileUrl;

    @NotNull
    private Long fileSizeBytes;

    @NotNull
    private String mimeType;

    @NotNull
    private String sha256Checksum;

    @NotNull
    private Instant uploadedAt;

    @NotNull
    private DisputeDTO dispute;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DisputeEvidenceType getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(DisputeEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha256Checksum() {
        return sha256Checksum;
    }

    public void setSha256Checksum(String sha256Checksum) {
        this.sha256Checksum = sha256Checksum;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public DisputeDTO getDispute() {
        return dispute;
    }

    public void setDispute(DisputeDTO dispute) {
        this.dispute = dispute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisputeEvidenceDTO)) {
            return false;
        }

        DisputeEvidenceDTO disputeEvidenceDTO = (DisputeEvidenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, disputeEvidenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeEvidenceDTO{" +
            "id=" + getId() +
            ", evidenceType='" + getEvidenceType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", mimeType='" + getMimeType() + "'" +
            ", sha256Checksum='" + getSha256Checksum() + "'" +
            ", uploadedAt='" + getUploadedAt() + "'" +
            ", dispute=" + getDispute() +
            "}";
    }
}
