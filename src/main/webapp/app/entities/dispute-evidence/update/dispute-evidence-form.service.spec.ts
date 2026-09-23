import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dispute-evidence.test-samples';

import { DisputeEvidenceFormService } from './dispute-evidence-form.service';

describe('DisputeEvidence Form Service', () => {
  let service: DisputeEvidenceFormService;

  beforeEach(() => {
    service = TestBed.inject(DisputeEvidenceFormService);
  });

  describe('Service methods', () => {
    describe('createDisputeEvidenceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDisputeEvidenceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            evidenceType: expect.any(Object),
            fileName: expect.any(Object),
            fileUrl: expect.any(Object),
            fileSizeBytes: expect.any(Object),
            mimeType: expect.any(Object),
            sha256Checksum: expect.any(Object),
            uploadedAt: expect.any(Object),
            dispute: expect.any(Object),
          }),
        );
      });

      it('passing IDisputeEvidence should create a new form with FormGroup', () => {
        const formGroup = service.createDisputeEvidenceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            evidenceType: expect.any(Object),
            fileName: expect.any(Object),
            fileUrl: expect.any(Object),
            fileSizeBytes: expect.any(Object),
            mimeType: expect.any(Object),
            sha256Checksum: expect.any(Object),
            uploadedAt: expect.any(Object),
            dispute: expect.any(Object),
          }),
        );
      });
    });

    describe('getDisputeEvidence', () => {
      it('should return NewDisputeEvidence for default DisputeEvidence initial value', () => {
        const formGroup = service.createDisputeEvidenceFormGroup(sampleWithNewData);

        const disputeEvidence = service.getDisputeEvidence(formGroup);

        expect(disputeEvidence).toMatchObject(sampleWithNewData);
      });

      it('should return NewDisputeEvidence for empty DisputeEvidence initial value', () => {
        const formGroup = service.createDisputeEvidenceFormGroup();

        const disputeEvidence = service.getDisputeEvidence(formGroup);

        expect(disputeEvidence).toMatchObject({});
      });

      it('should return IDisputeEvidence', () => {
        const formGroup = service.createDisputeEvidenceFormGroup(sampleWithRequiredData);

        const disputeEvidence = service.getDisputeEvidence(formGroup);

        expect(disputeEvidence).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDisputeEvidence should not enable id FormControl', () => {
        const formGroup = service.createDisputeEvidenceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDisputeEvidence should disable id FormControl', () => {
        const formGroup = service.createDisputeEvidenceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
