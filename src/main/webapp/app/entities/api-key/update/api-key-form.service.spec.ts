import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../api-key.test-samples';

import { ApiKeyFormService } from './api-key-form.service';

describe('ApiKey Form Service', () => {
  let service: ApiKeyFormService;

  beforeEach(() => {
    service = TestBed.inject(ApiKeyFormService);
  });

  describe('Service methods', () => {
    describe('createApiKeyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createApiKeyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            keyPrefix: expect.any(Object),
            keyHash: expect.any(Object),
            environment: expect.any(Object),
            isActive: expect.any(Object),
            issuedAt: expect.any(Object),
            revokedAt: expect.any(Object),
            graceExpiresAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });

      it('passing IApiKey should create a new form with FormGroup', () => {
        const formGroup = service.createApiKeyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            keyPrefix: expect.any(Object),
            keyHash: expect.any(Object),
            environment: expect.any(Object),
            isActive: expect.any(Object),
            issuedAt: expect.any(Object),
            revokedAt: expect.any(Object),
            graceExpiresAt: expect.any(Object),
            tenant: expect.any(Object),
          }),
        );
      });
    });

    describe('getApiKey', () => {
      it('should return NewApiKey for default ApiKey initial value', () => {
        const formGroup = service.createApiKeyFormGroup(sampleWithNewData);

        const apiKey = service.getApiKey(formGroup);

        expect(apiKey).toMatchObject(sampleWithNewData);
      });

      it('should return NewApiKey for empty ApiKey initial value', () => {
        const formGroup = service.createApiKeyFormGroup();

        const apiKey = service.getApiKey(formGroup);

        expect(apiKey).toMatchObject({});
      });

      it('should return IApiKey', () => {
        const formGroup = service.createApiKeyFormGroup(sampleWithRequiredData);

        const apiKey = service.getApiKey(formGroup);

        expect(apiKey).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IApiKey should not enable id FormControl', () => {
        const formGroup = service.createApiKeyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewApiKey should disable id FormControl', () => {
        const formGroup = service.createApiKeyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
