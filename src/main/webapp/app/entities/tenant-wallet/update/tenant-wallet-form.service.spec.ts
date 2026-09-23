import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../tenant-wallet.test-samples';

import { TenantWalletFormService } from './tenant-wallet-form.service';

describe('TenantWallet Form Service', () => {
  let service: TenantWalletFormService;

  beforeEach(() => {
    service = TestBed.inject(TenantWalletFormService);
  });

  describe('Service methods', () => {
    describe('createTenantWalletFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTenantWalletFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            currencyCode: expect.any(Object),
            availableBalance: expect.any(Object),
            lockedBalance: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing ITenantWallet should create a new form with FormGroup', () => {
        const formGroup = service.createTenantWalletFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            currencyCode: expect.any(Object),
            availableBalance: expect.any(Object),
            lockedBalance: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getTenantWallet', () => {
      it('should return NewTenantWallet for default TenantWallet initial value', () => {
        const formGroup = service.createTenantWalletFormGroup(sampleWithNewData);

        const tenantWallet = service.getTenantWallet(formGroup);

        expect(tenantWallet).toMatchObject(sampleWithNewData);
      });

      it('should return NewTenantWallet for empty TenantWallet initial value', () => {
        const formGroup = service.createTenantWalletFormGroup();

        const tenantWallet = service.getTenantWallet(formGroup);

        expect(tenantWallet).toMatchObject({});
      });

      it('should return ITenantWallet', () => {
        const formGroup = service.createTenantWalletFormGroup(sampleWithRequiredData);

        const tenantWallet = service.getTenantWallet(formGroup);

        expect(tenantWallet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITenantWallet should not enable id FormControl', () => {
        const formGroup = service.createTenantWalletFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTenantWallet should disable id FormControl', () => {
        const formGroup = service.createTenantWalletFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
