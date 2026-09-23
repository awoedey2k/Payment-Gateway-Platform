import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../settlement-batch.test-samples';

import { SettlementBatchFormService } from './settlement-batch-form.service';

describe('SettlementBatch Form Service', () => {
  let service: SettlementBatchFormService;

  beforeEach(() => {
    service = TestBed.inject(SettlementBatchFormService);
  });

  describe('Service methods', () => {
    describe('createSettlementBatchFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSettlementBatchFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            status: expect.any(Object),
            totalAmount: expect.any(Object),
            currencyCode: expect.any(Object),
            scheduledAt: expect.any(Object),
            completedAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing ISettlementBatch should create a new form with FormGroup', () => {
        const formGroup = service.createSettlementBatchFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            status: expect.any(Object),
            totalAmount: expect.any(Object),
            currencyCode: expect.any(Object),
            scheduledAt: expect.any(Object),
            completedAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getSettlementBatch', () => {
      it('should return NewSettlementBatch for default SettlementBatch initial value', () => {
        const formGroup = service.createSettlementBatchFormGroup(sampleWithNewData);

        const settlementBatch = service.getSettlementBatch(formGroup);

        expect(settlementBatch).toMatchObject(sampleWithNewData);
      });

      it('should return NewSettlementBatch for empty SettlementBatch initial value', () => {
        const formGroup = service.createSettlementBatchFormGroup();

        const settlementBatch = service.getSettlementBatch(formGroup);

        expect(settlementBatch).toMatchObject({});
      });

      it('should return ISettlementBatch', () => {
        const formGroup = service.createSettlementBatchFormGroup(sampleWithRequiredData);

        const settlementBatch = service.getSettlementBatch(formGroup);

        expect(settlementBatch).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISettlementBatch should not enable id FormControl', () => {
        const formGroup = service.createSettlementBatchFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSettlementBatch should disable id FormControl', () => {
        const formGroup = service.createSettlementBatchFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
