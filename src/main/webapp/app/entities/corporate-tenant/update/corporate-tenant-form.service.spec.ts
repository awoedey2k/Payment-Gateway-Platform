import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../corporate-tenant.test-samples';

import { CorporateTenantFormService } from './corporate-tenant-form.service';

describe('CorporateTenant Form Service', () => {
  let service: CorporateTenantFormService;

  beforeEach(() => {
    service = TestBed.inject(CorporateTenantFormService);
  });

  describe('Service methods', () => {
    describe('createCorporateTenantFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCorporateTenantFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            legalBusinessName: expect.any(Object),
            businessRegistrationNumber: expect.any(Object),
            taxIdentificationNumber: expect.any(Object),
            operatingJurisdiction: expect.any(Object),
            status: expect.any(Object),
            kycStatus: expect.any(Object),
            riskScore: expect.any(Object),
            createdAt: expect.any(Object),
            activatedAt: expect.any(Object),
          }),
        );
      });

      it('passing ICorporateTenant should create a new form with FormGroup', () => {
        const formGroup = service.createCorporateTenantFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            legalBusinessName: expect.any(Object),
            businessRegistrationNumber: expect.any(Object),
            taxIdentificationNumber: expect.any(Object),
            operatingJurisdiction: expect.any(Object),
            status: expect.any(Object),
            kycStatus: expect.any(Object),
            riskScore: expect.any(Object),
            createdAt: expect.any(Object),
            activatedAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getCorporateTenant', () => {
      it('should return NewCorporateTenant for default CorporateTenant initial value', () => {
        const formGroup = service.createCorporateTenantFormGroup(sampleWithNewData);

        const corporateTenant = service.getCorporateTenant(formGroup);

        expect(corporateTenant).toMatchObject(sampleWithNewData);
      });

      it('should return NewCorporateTenant for empty CorporateTenant initial value', () => {
        const formGroup = service.createCorporateTenantFormGroup();

        const corporateTenant = service.getCorporateTenant(formGroup);

        expect(corporateTenant).toMatchObject({});
      });

      it('should return ICorporateTenant', () => {
        const formGroup = service.createCorporateTenantFormGroup(sampleWithRequiredData);

        const corporateTenant = service.getCorporateTenant(formGroup);

        expect(corporateTenant).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICorporateTenant should not enable id FormControl', () => {
        const formGroup = service.createCorporateTenantFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCorporateTenant should disable id FormControl', () => {
        const formGroup = service.createCorporateTenantFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
