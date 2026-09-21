import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tenant-domain.test-samples';

import { TenantDomainFormService } from './tenant-domain-form.service';

describe('TenantDomain Form Service', () => {
  let service: TenantDomainFormService;

  beforeEach(() => {
    service = TestBed.inject(TenantDomainFormService);
  });

  describe('Service methods', () => {
    describe('createTenantDomainFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantDomainFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            customDomain: expect.any(Object),
            supportedLocales: expect.any(Object),
            isVerified: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing ITenantDomain should create a new form with FormGroup', () => {
        const formGroup = service.createTenantDomainFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            customDomain: expect.any(Object),
            supportedLocales: expect.any(Object),
            isVerified: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenantDomain', () => {
      it('should return NewTenantDomain for default TenantDomain initial value', () => {
        const formGroup = service.createTenantDomainFormGroup(sampleWithNewData);

        const tenantDomain = service.getTenantDomain(formGroup);

        expect(tenantDomain).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenantDomain for empty TenantDomain initial value', () => {
        const formGroup = service.createTenantDomainFormGroup();

        const tenantDomain = service.getTenantDomain(formGroup);

        expect(tenantDomain).toMatchObject({});
      });

      it('should return ITenantDomain', () => {
        const formGroup = service.createTenantDomainFormGroup(sampleWithRequiredData);

        const tenantDomain = service.getTenantDomain(formGroup);

        expect(tenantDomain).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenantDomain should not enable id FormControl', () => {
        const formGroup = service.createTenantDomainFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenantDomain should disable id FormControl', () => {
        const formGroup = service.createTenantDomainFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
