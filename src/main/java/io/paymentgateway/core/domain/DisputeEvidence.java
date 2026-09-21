package io.paymentgateway.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.paymentgateway.core.domain.enumeration.DisputeEvidenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DisputeEvidence.
 */
@Entity
@Table(name = "dispute_evidence")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeEvidence implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "evidence_type", nullable = false)
    private DisputeEvidenceType evidenceType;

    @NotNull
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull
    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @NotNull
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @NotNull
    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @NotNull
    @Column(name = "sha_256_checksum", nullable = false)
    private String sha256Checksum;

    @NotNull
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "tenant", "transaction" }, allowSetters = true)
    private Dispute dispute;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DisputeEvidence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DisputeEvidenceType getEvidenceType() {
        return this.evidenceType;
    }

    public DisputeEvidence evidenceType(DisputeEvidenceType evidenceType) {
        this.setEvidenceType(evidenceType);
        return this;
    }

    public void setEvidenceType(DisputeEvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public DisputeEvidence fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public DisputeEvidence fileUrl(String fileUrl) {
        this.setFileUrl(fileUrl);
        return this;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSizeBytes() {
        return this.fileSizeBytes;
    }

    public DisputeEvidence fileSizeBytes(Long fileSizeBytes) {
        this.setFileSizeBytes(fileSizeBytes);
        return this;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public DisputeEvidence mimeType(String mimeType) {
        this.setMimeType(mimeType);
        return this;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha256Checksum() {
        return this.sha256Checksum;
    }

    public DisputeEvidence sha256Checksum(String sha256Checksum) {
        this.setSha256Checksum(sha256Checksum);
        return this;
    }

    public void setSha256Checksum(String sha256Checksum) {
        this.sha256Checksum = sha256Checksum;
    }

    public Instant getUploadedAt() {
        return this.uploadedAt;
    }

    public DisputeEvidence uploadedAt(Instant uploadedAt) {
        this.setUploadedAt(uploadedAt);
        return this;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Dispute getDispute() {
        return this.dispute;
    }

    public void setDispute(Dispute dispute) {
        this.dispute = dispute;
    }

    public DisputeEvidence dispute(Dispute dispute) {
        this.setDispute(dispute);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisputeEvidence)) {
            return false;
        }
        return getId() != null && getId().equals(((DisputeEvidence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeEvidence{" +
            "id=" + getId() +
            ", evidenceType='" + getEvidenceType() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", mimeType='" + getMimeType() + "'" +
            ", sha256Checksum='" + getSha256Checksum() + "'" +
            ", uploadedAt='" + getUploadedAt() + "'" +
            "}";
    }
}
