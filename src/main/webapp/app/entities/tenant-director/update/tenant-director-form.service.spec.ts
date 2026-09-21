import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tenant-director.test-samples';

import { TenantDirectorFormService } from './tenant-director-form.service';

describe('TenantDirector Form Service', () => {
  let service: TenantDirectorFormService;

  beforeEach(() => {
    service = TestBed.inject(TenantDirectorFormService);
  });

  describe('Service methods', () => {
    describe('createTenantDirectorFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantDirectorFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            dateOfBirth: expect.any(Object),
            nationality: expect.any(Object),
            identificationType: expect.any(Object),
            identificationNumber: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing ITenantDirector should create a new form with FormGroup', () => {
        const formGroup = service.createTenantDirectorFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            dateOfBirth: expect.any(Object),
            nationality: expect.any(Object),
            identificationType: expect.any(Object),
            identificationNumber: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenantDirector', () => {
      it('should return NewTenantDirector for default TenantDirector initial value', () => {
        const formGroup = service.createTenantDirectorFormGroup(sampleWithNewData);

        const tenantDirector = service.getTenantDirector(formGroup);

        expect(tenantDirector).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenantDirector for empty TenantDirector initial value', () => {
        const formGroup = service.createTenantDirectorFormGroup();

        const tenantDirector = service.getTenantDirector(formGroup);

        expect(tenantDirector).toMatchObject({});
      });

      it('should return ITenantDirector', () => {
        const formGroup = service.createTenantDirectorFormGroup(sampleWithRequiredData);

        const tenantDirector = service.getTenantDirector(formGroup);

        expect(tenantDirector).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenantDirector should not enable id FormControl', () => {
        const formGroup = service.createTenantDirectorFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenantDirector should disable id FormControl', () => {
        const formGroup = service.createTenantDirectorFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
