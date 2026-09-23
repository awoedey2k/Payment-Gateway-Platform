import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ledger-account.test-samples';

import { LedgerAccountFormService } from './ledger-account-form.service';

describe('LedgerAccount Form Service', () => {
  let service: LedgerAccountFormService;

  beforeEach(() => {
    service = TestBed.inject(LedgerAccountFormService);
  });

  describe('Service methods', () => {
    describe('createLedgerAccountFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLedgerAccountFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accountCode: expect.any(Object),
            accountType: expect.any(Object),
            currencyCode: expect.any(Object),
          }),
        );
      });

      it('passing ILedgerAccount should create a new form with FormGroup', () => {
        const formGroup = service.createLedgerAccountFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accountCode: expect.any(Object),
            accountType: expect.any(Object),
            currencyCode: expect.any(Object),
          }),
        );
      });
    });

    describe('getLedgerAccount', () => {
      it('should return NewLedgerAccount for default LedgerAccount initial value', () => {
        const formGroup = service.createLedgerAccountFormGroup(sampleWithNewData);

        const ledgerAccount = service.getLedgerAccount(formGroup);

        expect(ledgerAccount).toMatchObject(sampleWithNewData);
      });

      it('should return NewLedgerAccount for empty LedgerAccount initial value', () => {
        const formGroup = service.createLedgerAccountFormGroup();

        const ledgerAccount = service.getLedgerAccount(formGroup);

        expect(ledgerAccount).toMatchObject({});
      });

      it('should return ILedgerAccount', () => {
        const formGroup = service.createLedgerAccountFormGroup(sampleWithRequiredData);

        const ledgerAccount = service.getLedgerAccount(formGroup);

        expect(ledgerAccount).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILedgerAccount should not enable id FormControl', () => {
        const formGroup = service.createLedgerAccountFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLedgerAccount should disable id FormControl', () => {
        const formGroup = service.createLedgerAccountFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
