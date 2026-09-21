import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../forex-rate.test-samples';

import { ForexRateFormService } from './forex-rate-form.service';

describe('ForexRate Form Service', () => {
  let service: ForexRateFormService;

  beforeEach(() => {
    service = TestBed.inject(ForexRateFormService);
  });

  describe('Service methods', () => {
    describe('createForexRateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createForexRateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            baseCurrency: expect.any(Object),
            quoteCurrency: expect.any(Object),
            rate: expect.any(Object),
            platformSpreadBps: expect.any(Object),
            lockedAt: expect.any(Object),
            expiresAt: expect.any(Object),
          }),
        );
      });

      it('passing IForexRate should create a new form with FormGroup', () => {
        const formGroup = service.createForexRateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            baseCurrency: expect.any(Object),
            quoteCurrency: expect.any(Object),
            rate: expect.any(Object),
            platformSpreadBps: expect.any(Object),
            lockedAt: expect.any(Object),
            expiresAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getForexRate', () => {
      it('should return NewForexRate for default ForexRate initial value', () => {
        const formGroup = service.createForexRateFormGroup(sampleWithNewData);

        const forexRate = service.getForexRate(formGroup);

        expect(forexRate).toMatchObject(sampleWithNewData);
      });

      it('should return NewForexRate for empty ForexRate initial value', () => {
        const formGroup = service.createForexRateFormGroup();

        const forexRate = service.getForexRate(formGroup);

        expect(forexRate).toMatchObject({});
      });

      it('should return IForexRate', () => {
        const formGroup = service.createForexRateFormGroup(sampleWithRequiredData);

        const forexRate = service.getForexRate(formGroup);

        expect(forexRate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IForexRate should not enable id FormControl', () => {
        const formGroup = service.createForexRateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewForexRate should disable id FormControl', () => {
        const formGroup = service.createForexRateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
