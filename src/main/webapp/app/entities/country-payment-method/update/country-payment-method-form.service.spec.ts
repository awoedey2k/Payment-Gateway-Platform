import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../country-payment-method.test-samples';

import { CountryPaymentMethodFormService } from './country-payment-method-form.service';

describe('CountryPaymentMethod Form Service', () => {
  let service: CountryPaymentMethodFormService;

  beforeEach(() => {
    service = TestBed.inject(CountryPaymentMethodFormService);
  });

  describe('Service methods', () => {
    describe('createCountryPaymentMethodFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            minTxnAmount: expect.any(Object),
            maxTxnAmount: expect.any(Object),
            supportsRecurring: expect.any(Object),
            supportsInstantRefund: expect.any(Object),
            isActive: expect.any(Object),
            country: expect.any(Object),
            paymentMethod: expect.any(Object),
          }),
        );
      });

      it('passing ICountryPaymentMethod should create a new form with FormGroup', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            minTxnAmount: expect.any(Object),
            maxTxnAmount: expect.any(Object),
            supportsRecurring: expect.any(Object),
            supportsInstantRefund: expect.any(Object),
            isActive: expect.any(Object),
            country: expect.any(Object),
            paymentMethod: expect.any(Object),
          }),
        );
      });
    });

    describe('getCountryPaymentMethod', () => {
      it('should return NewCountryPaymentMethod for default CountryPaymentMethod initial value', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup(sampleWithNewData);

        const countryPaymentMethod = service.getCountryPaymentMethod(formGroup);

        expect(countryPaymentMethod).toMatchObject(sampleWithNewData);
      });

      it('should return NewCountryPaymentMethod for empty CountryPaymentMethod initial value', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup();

        const countryPaymentMethod = service.getCountryPaymentMethod(formGroup);

        expect(countryPaymentMethod).toMatchObject({});
      });

      it('should return ICountryPaymentMethod', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup(sampleWithRequiredData);

        const countryPaymentMethod = service.getCountryPaymentMethod(formGroup);

        expect(countryPaymentMethod).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICountryPaymentMethod should not enable id FormControl', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCountryPaymentMethod should disable id FormControl', () => {
        const formGroup = service.createCountryPaymentMethodFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
