import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../refund.test-samples';

import { RefundFormService } from './refund-form.service';

describe('Refund Form Service', () => {
  let service: RefundFormService;

  beforeEach(() => {
    service = TestBed.inject(RefundFormService);
  });

  describe('Service methods', () => {
    describe('createRefundFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRefundFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
            status: expect.any(Object),
            createdAt: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });

      it('passing IRefund should create a new form with FormGroup', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
            status: expect.any(Object),
            createdAt: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });
    });

    describe('getRefund', () => {
      it('should return NewRefund for default Refund initial value', () => {
        const formGroup = service.createRefundFormGroup(sampleWithNewData);

        const refund = service.getRefund(formGroup);

        expect(refund).toMatchObject(sampleWithNewData);
      });

      it('should return NewRefund for empty Refund initial value', () => {
        const formGroup = service.createRefundFormGroup();

        const refund = service.getRefund(formGroup);

        expect(refund).toMatchObject({});
      });

      it('should return IRefund', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);

        const refund = service.getRefund(formGroup);

        expect(refund).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRefund should not enable id FormControl', () => {
        const formGroup = service.createRefundFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRefund should disable id FormControl', () => {
        const formGroup = service.createRefundFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
