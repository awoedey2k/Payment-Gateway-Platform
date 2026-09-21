import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../aml-check.test-samples';

import { AmlCheckFormService } from './aml-check-form.service';

describe('AmlCheck Form Service', () => {
  let service: AmlCheckFormService;

  beforeEach(() => {
    service = TestBed.inject(AmlCheckFormService);
  });

  describe('Service methods', () => {
    describe('createAmlCheckFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAmlCheckFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            riskScore: expect.any(Object),
            decision: expect.any(Object),
            ruleTriggered: expect.any(Object),
            checkedAt: expect.any(Object),
            tenant: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });

      it('passing IAmlCheck should create a new form with FormGroup', () => {
        const formGroup = service.createAmlCheckFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            riskScore: expect.any(Object),
            decision: expect.any(Object),
            ruleTriggered: expect.any(Object),
            checkedAt: expect.any(Object),
            tenant: expect.any(Object),
            transaction: expect.any(Object),
          }),
        );
      });
    });

    describe('getAmlCheck', () => {
      it('should return NewAmlCheck for default AmlCheck initial value', () => {
        const formGroup = service.createAmlCheckFormGroup(sampleWithNewData);

        const amlCheck = service.getAmlCheck(formGroup);

        expect(amlCheck).toMatchObject(sampleWithNewData);
      });

      it('should return NewAmlCheck for empty AmlCheck initial value', () => {
        const formGroup = service.createAmlCheckFormGroup();

        const amlCheck = service.getAmlCheck(formGroup);

        expect(amlCheck).toMatchObject({});
      });

      it('should return IAmlCheck', () => {
        const formGroup = service.createAmlCheckFormGroup(sampleWithRequiredData);

        const amlCheck = service.getAmlCheck(formGroup);

        expect(amlCheck).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAmlCheck should not enable id FormControl', () => {
        const formGroup = service.createAmlCheckFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAmlCheck should disable id FormControl', () => {
        const formGroup = service.createAmlCheckFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
