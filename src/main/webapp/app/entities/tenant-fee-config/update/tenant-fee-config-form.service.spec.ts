import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tenant-fee-config.test-samples';

import { TenantFeeConfigFormService } from './tenant-fee-config-form.service';

describe('TenantFeeConfig Form Service', () => {
  let service: TenantFeeConfigFormService;

  beforeEach(() => {
    service = TestBed.inject(TenantFeeConfigFormService);
  });

  describe('Service methods', () => {
    describe('createTenantFeeConfigFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantFeeConfigFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fixedFee: expect.any(Object),
            percentageFee: expect.any(Object),
            capAmount: expect.any(Object),
            feeBearer: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
            countryPaymentMethod: expect.any(Object),
          }),
        );
      });

      it('passing ITenantFeeConfig should create a new form with FormGroup', () => {
        const formGroup = service.createTenantFeeConfigFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fixedFee: expect.any(Object),
            percentageFee: expect.any(Object),
            capAmount: expect.any(Object),
            feeBearer: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
            countryPaymentMethod: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenantFeeConfig', () => {
      it('should return NewTenantFeeConfig for default TenantFeeConfig initial value', () => {
        const formGroup = service.createTenantFeeConfigFormGroup(sampleWithNewData);

        const tenantFeeConfig = service.getTenantFeeConfig(formGroup);

        expect(tenantFeeConfig).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenantFeeConfig for empty TenantFeeConfig initial value', () => {
        const formGroup = service.createTenantFeeConfigFormGroup();

        const tenantFeeConfig = service.getTenantFeeConfig(formGroup);

        expect(tenantFeeConfig).toMatchObject({});
      });

      it('should return ITenantFeeConfig', () => {
        const formGroup = service.createTenantFeeConfigFormGroup(sampleWithRequiredData);

        const tenantFeeConfig = service.getTenantFeeConfig(formGroup);

        expect(tenantFeeConfig).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenantFeeConfig should not enable id FormControl', () => {
        const formGroup = service.createTenantFeeConfigFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenantFeeConfig should disable id FormControl', () => {
        const formGroup = service.createTenantFeeConfigFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
