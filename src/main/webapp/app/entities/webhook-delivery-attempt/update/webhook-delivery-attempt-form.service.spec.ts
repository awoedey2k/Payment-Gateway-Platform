import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../webhook-delivery-attempt.test-samples';

import { WebhookDeliveryAttemptFormService } from './webhook-delivery-attempt-form.service';

describe('WebhookDeliveryAttempt Form Service', () => {
  let service: WebhookDeliveryAttemptFormService;

  beforeEach(() => {
    service = TestBed.inject(WebhookDeliveryAttemptFormService);
  });

  describe('Service methods', () => {
    describe('createWebhookDeliveryAttemptFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            eventType: expect.any(Object),
            status: expect.any(Object),
            httpStatusCode: expect.any(Object),
            attemptNumber: expect.any(Object),
            attemptedAt: expect.any(Object),
            subscription: expect.any(Object),
          }),
        );
      });

      it('passing IWebhookDeliveryAttempt should create a new form with FormGroup', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            eventType: expect.any(Object),
            status: expect.any(Object),
            httpStatusCode: expect.any(Object),
            attemptNumber: expect.any(Object),
            attemptedAt: expect.any(Object),
            subscription: expect.any(Object),
          }),
        );
      });
    });

    describe('getWebhookDeliveryAttempt', () => {
      it('should return NewWebhookDeliveryAttempt for default WebhookDeliveryAttempt initial value', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup(sampleWithNewData);

        const webhookDeliveryAttempt = service.getWebhookDeliveryAttempt(formGroup);

        expect(webhookDeliveryAttempt).toMatchObject(sampleWithNewData);
      });

      it('should return NewWebhookDeliveryAttempt for empty WebhookDeliveryAttempt initial value', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup();

        const webhookDeliveryAttempt = service.getWebhookDeliveryAttempt(formGroup);

        expect(webhookDeliveryAttempt).toMatchObject({});
      });

      it('should return IWebhookDeliveryAttempt', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup(sampleWithRequiredData);

        const webhookDeliveryAttempt = service.getWebhookDeliveryAttempt(formGroup);

        expect(webhookDeliveryAttempt).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWebhookDeliveryAttempt should not enable id FormControl', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWebhookDeliveryAttempt should disable id FormControl', () => {
        const formGroup = service.createWebhookDeliveryAttemptFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
