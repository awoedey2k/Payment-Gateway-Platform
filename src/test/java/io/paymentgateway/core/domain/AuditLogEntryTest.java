package io.paymentgateway.core.domain;

import static io.paymentgateway.core.domain.AuditLogEntryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditLogEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditLogEntry.class);
        AuditLogEntry auditLogEntry1 = getAuditLogEntrySample1();
        AuditLogEntry auditLogEntry2 = new AuditLogEntry();
        assertThat(auditLogEntry1).isNotEqualTo(auditLogEntry2);

        auditLogEntry2.setId(auditLogEntry1.getId());
        assertThat(auditLogEntry1).isEqualTo(auditLogEntry2);

        auditLogEntry2 = getAuditLogEntrySample2();
        assertThat(auditLogEntry1).isNotEqualTo(auditLogEntry2);
    }
}
