import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../transaction.test-samples';

import { TransactionFormService } from './transaction-form.service';

describe('Transaction Form Service', () => {
  let service: TransactionFormService;

  beforeEach(() => {
    service = TestBed.inject(TransactionFormService);
  });

  describe('Service methods', () => {
    describe('createTransactionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTransactionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            tenantReference: expect.any(Object),
            status: expect.any(Object),
            amount: expect.any(Object),
            feeAmount: expect.any(Object),
            netAmount: expect.any(Object),
            currencyCode: expect.any(Object),
            countryCode: expect.any(Object),
            paymentMethodCode: expect.any(Object),
            idempotencyKey: expect.any(Object),
            customerEmail: expect.any(Object),
            customerPhone: expect.any(Object),
            createdAt: expect.any(Object),
            completedAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing ITransaction should create a new form with FormGroup', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            tenantReference: expect.any(Object),
            status: expect.any(Object),
            amount: expect.any(Object),
            feeAmount: expect.any(Object),
            netAmount: expect.any(Object),
            currencyCode: expect.any(Object),
            countryCode: expect.any(Object),
            paymentMethodCode: expect.any(Object),
            idempotencyKey: expect.any(Object),
            customerEmail: expect.any(Object),
            customerPhone: expect.any(Object),
            createdAt: expect.any(Object),
            completedAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getTransaction', () => {
      it('should return NewTransaction for default Transaction initial value', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithNewData);

        const transaction = service.getTransaction(formGroup);

        expect(transaction).toMatchObject(sampleWithNewData);
      });

      it('should return NewTransaction for empty Transaction initial value', () => {
        const formGroup = service.createTransactionFormGroup();

        const transaction = service.getTransaction(formGroup);

        expect(transaction).toMatchObject({});
      });

      it('should return ITransaction', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);

        const transaction = service.getTransaction(formGroup);

        expect(transaction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITransaction should not enable id FormControl', () => {
        const formGroup = service.createTransactionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTransaction should disable id FormControl', () => {
        const formGroup = service.createTransactionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
