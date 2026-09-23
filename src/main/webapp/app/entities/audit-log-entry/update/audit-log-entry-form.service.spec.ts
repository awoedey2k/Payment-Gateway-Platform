import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../audit-log-entry.test-samples';

import { AuditLogEntryFormService } from './audit-log-entry-form.service';

describe('AuditLogEntry Form Service', () => {
  let service: AuditLogEntryFormService;

  beforeEach(() => {
    service = TestBed.inject(AuditLogEntryFormService);
  });

  describe('Service methods', () => {
    describe('createAuditLogEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAuditLogEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            actorType: expect.any(Object),
            actorId: expect.any(Object),
            action: expect.any(Object),
            entityType: expect.any(Object),
            entityId: expect.any(Object),
            previousHash: expect.any(Object),
            entryHash: expect.any(Object),
            recordedAt: expect.any(Object),
          }),
        );
      });

      it('passing IAuditLogEntry should create a new form with FormGroup', () => {
        const formGroup = service.createAuditLogEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            actorType: expect.any(Object),
            actorId: expect.any(Object),
            action: expect.any(Object),
            entityType: expect.any(Object),
            entityId: expect.any(Object),
            previousHash: expect.any(Object),
            entryHash: expect.any(Object),
            recordedAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getAuditLogEntry', () => {
      it('should return NewAuditLogEntry for default AuditLogEntry initial value', () => {
        const formGroup = service.createAuditLogEntryFormGroup(sampleWithNewData);

        const auditLogEntry = service.getAuditLogEntry(formGroup);

        expect(auditLogEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewAuditLogEntry for empty AuditLogEntry initial value', () => {
        const formGroup = service.createAuditLogEntryFormGroup();

        const auditLogEntry = service.getAuditLogEntry(formGroup);

        expect(auditLogEntry).toMatchObject({});
      });

      it('should return IAuditLogEntry', () => {
        const formGroup = service.createAuditLogEntryFormGroup(sampleWithRequiredData);

        const auditLogEntry = service.getAuditLogEntry(formGroup);

        expect(auditLogEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAuditLogEntry should not enable id FormControl', () => {
        const formGroup = service.createAuditLogEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAuditLogEntry should disable id FormControl', () => {
        const formGroup = service.createAuditLogEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
