import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../routing-rule.test-samples';

import { RoutingRuleFormService } from './routing-rule-form.service';

describe('RoutingRule Form Service', () => {
  let service: RoutingRuleFormService;

  beforeEach(() => {
    service = TestBed.inject(RoutingRuleFormService);
  });

  describe('Service methods', () => {
    describe('createRoutingRuleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRoutingRuleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            priority: expect.any(Object),
            scope: expect.any(Object),
            countryCode: expect.any(Object),
            currencyCode: expect.any(Object),
            cardBrand: expect.any(Object),
            primaryAdapter: expect.any(Object),
            fallbackAdapter: expect.any(Object),
            maxRetries: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing IRoutingRule should create a new form with FormGroup', () => {
        const formGroup = service.createRoutingRuleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            priority: expect.any(Object),
            scope: expect.any(Object),
            countryCode: expect.any(Object),
            currencyCode: expect.any(Object),
            cardBrand: expect.any(Object),
            primaryAdapter: expect.any(Object),
            fallbackAdapter: expect.any(Object),
            maxRetries: expect.any(Object),
            isActive: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getRoutingRule', () => {
      it('should return NewRoutingRule for default RoutingRule initial value', () => {
        const formGroup = service.createRoutingRuleFormGroup(sampleWithNewData);

        const routingRule = service.getRoutingRule(formGroup);

        expect(routingRule).toMatchObject(sampleWithNewData);
      });

      it('should return NewRoutingRule for empty RoutingRule initial value', () => {
        const formGroup = service.createRoutingRuleFormGroup();

        const routingRule = service.getRoutingRule(formGroup);

        expect(routingRule).toMatchObject({});
      });

      it('should return IRoutingRule', () => {
        const formGroup = service.createRoutingRuleFormGroup(sampleWithRequiredData);

        const routingRule = service.getRoutingRule(formGroup);

        expect(routingRule).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRoutingRule should not enable id FormControl', () => {
        const formGroup = service.createRoutingRuleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRoutingRule should disable id FormControl', () => {
        const formGroup = service.createRoutingRuleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
