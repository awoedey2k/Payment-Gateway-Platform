package io.paymentgateway.core.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.paymentgateway.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditLogEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditLogEntryDTO.class);
        AuditLogEntryDTO auditLogEntryDTO1 = new AuditLogEntryDTO();
        auditLogEntryDTO1.setId(1L);
        AuditLogEntryDTO auditLogEntryDTO2 = new AuditLogEntryDTO();
        assertThat(auditLogEntryDTO1).isNotEqualTo(auditLogEntryDTO2);
        auditLogEntryDTO2.setId(auditLogEntryDTO1.getId());
        assertThat(auditLogEntryDTO1).isEqualTo(auditLogEntryDTO2);
        auditLogEntryDTO2.setId(2L);
        assertThat(auditLogEntryDTO1).isNotEqualTo(auditLogEntryDTO2);
        auditLogEntryDTO1.setId(null);
        assertThat(auditLogEntryDTO1).isNotEqualTo(auditLogEntryDTO2);
    }
}
