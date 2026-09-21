package io.paymentgateway.core.service.mapper;

import static io.paymentgateway.core.domain.AuditLogEntryAsserts.*;
import static io.paymentgateway.core.domain.AuditLogEntryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditLogEntryMapperTest {

    private AuditLogEntryMapper auditLogEntryMapper;

    @BeforeEach
    void setUp() {
        auditLogEntryMapper = new AuditLogEntryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAuditLogEntrySample1();
        var actual = auditLogEntryMapper.toEntity(auditLogEntryMapper.toDto(expected));
        assertAuditLogEntryAllPropertiesEquals(expected, actual);
    }
}
