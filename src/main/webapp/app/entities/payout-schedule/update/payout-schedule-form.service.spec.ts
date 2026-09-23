import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../payout-schedule.test-samples';

import { PayoutScheduleFormService } from './payout-schedule-form.service';

describe('PayoutSchedule Form Service', () => {
  let service: PayoutScheduleFormService;

  beforeEach(() => {
    service = TestBed.inject(PayoutScheduleFormService);
  });

  describe('Service methods', () => {
    describe('createPayoutScheduleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPayoutScheduleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            frequencyMode: expect.any(Object),
            thresholdAmount: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing IPayoutSchedule should create a new form with FormGroup', () => {
        const formGroup = service.createPayoutScheduleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            frequencyMode: expect.any(Object),
            thresholdAmount: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getPayoutSchedule', () => {
      it('should return NewPayoutSchedule for default PayoutSchedule initial value', () => {
        const formGroup = service.createPayoutScheduleFormGroup(sampleWithNewData);

        const payoutSchedule = service.getPayoutSchedule(formGroup);

        expect(payoutSchedule).toMatchObject(sampleWithNewData);
      });

      it('should return NewPayoutSchedule for empty PayoutSchedule initial value', () => {
        const formGroup = service.createPayoutScheduleFormGroup();

        const payoutSchedule = service.getPayoutSchedule(formGroup);

        expect(payoutSchedule).toMatchObject({});
      });

      it('should return IPayoutSchedule', () => {
        const formGroup = service.createPayoutScheduleFormGroup(sampleWithRequiredData);

        const payoutSchedule = service.getPayoutSchedule(formGroup);

        expect(payoutSchedule).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPayoutSchedule should not enable id FormControl', () => {
        const formGroup = service.createPayoutScheduleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPayoutSchedule should disable id FormControl', () => {
        const formGroup = service.createPayoutScheduleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
