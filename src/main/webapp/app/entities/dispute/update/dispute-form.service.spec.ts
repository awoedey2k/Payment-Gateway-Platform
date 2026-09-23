import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dispute.test-samples';

import { DisputeFormService } from './dispute-form.service';

describe('Dispute Form Service', () => {
  let service: DisputeFormService;

  beforeEach(() => {
    service = TestBed.inject(DisputeFormService);
  });

  describe('Service methods', () => {
    describe('createDisputeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDisputeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            caseReference: expect.any(Object),
            amount: expect.any(Object),
            currencyCode: expect.any(Object),
            reasonCode: expect.any(Object),
            reasonDescription: expect.any(Object),
            status: expect.any(Object),
            dueDate: expect.any(Object),
            evidenceSubmittedAt: expect.any(Object),
            resolvedAt: expect.any(Object),
            tenant: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });

      it('passing IDispute should create a new form with FormGroup', () => {
        const formGroup = service.createDisputeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            caseReference: expect.any(Object),
            amount: expect.any(Object),
            currencyCode: expect.any(Object),
            reasonCode: expect.any(Object),
            reasonDescription: expect.any(Object),
            status: expect.any(Object),
            dueDate: expect.any(Object),
            evidenceSubmittedAt: expect.any(Object),
            resolvedAt: expect.any(Object),
            tenant: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });
    });

    describe('getDispute', () => {
      it('should return NewDispute for default Dispute initial value', () => {
        const formGroup = service.createDisputeFormGroup(sampleWithNewData);

        const dispute = service.getDispute(formGroup);

        expect(dispute).toMatchObject(sampleWithNewData);
      });

      it('should return NewDispute for empty Dispute initial value', () => {
        const formGroup = service.createDisputeFormGroup();

        const dispute = service.getDispute(formGroup);

        expect(dispute).toMatchObject({});
      });

      it('should return IDispute', () => {
        const formGroup = service.createDisputeFormGroup(sampleWithRequiredData);

        const dispute = service.getDispute(formGroup);

        expect(dispute).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDispute should not enable id FormControl', () => {
        const formGroup = service.createDisputeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDispute should disable id FormControl', () => {
        const formGroup = service.createDisputeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
