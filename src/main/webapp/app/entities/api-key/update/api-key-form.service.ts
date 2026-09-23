import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IApiKey, NewApiKey } from '../api-key.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IApiKey for edit and NewApiKeyFormGroupInput for create.
 */
type ApiKeyFormGroupInput = IApiKey | PartialWithRequiredKeyOf<NewApiKey>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IApiKey | NewApiKey> = Omit<T, 'issuedAt' | 'revokedAt' | 'graceExpiresAt'> & {
  issuedAt?: string | null;
  revokedAt?: string | null;
  graceExpiresAt?: string | null;
};

type ApiKeyFormRawValue = FormValueOf<IApiKey>;

type NewApiKeyFormRawValue = FormValueOf<NewApiKey>;

type ApiKeyFormDefaults = Pick<NewApiKey, 'id' | 'isActive' | 'issuedAt' | 'revokedAt' | 'graceExpiresAt'>;

type ApiKeyFormGroupContent = {
  id: FormControl<ApiKeyFormRawValue['id'] | NewApiKey['id']>;
  keyPrefix: FormControl<ApiKeyFormRawValue['keyPrefix']>;
  keyHash: FormControl<ApiKeyFormRawValue['keyHash']>;
  environment: FormControl<ApiKeyFormRawValue['environment']>;
  isActive: FormControl<ApiKeyFormRawValue['isActive']>;
  issuedAt: FormControl<ApiKeyFormRawValue['issuedAt']>;
  revokedAt: FormControl<ApiKeyFormRawValue['revokedAt']>;
  graceExpiresAt: FormControl<ApiKeyFormRawValue['graceExpiresAt']>;
  tenant: FormControl<ApiKeyFormRawValue['tenant']>;
};

export type ApiKeyFormGroup = FormGroup<ApiKeyFormGroupContent>;

@Service()
export class ApiKeyFormService {
  createApiKeyFormGroup(apiKey?: ApiKeyFormGroupInput): ApiKeyFormGroup {
    const apiKeyRawValue = this.convertApiKeyToApiKeyRawValue({
      ...this.getFormDefaults(),
      ...(apiKey ?? { id: null }),
    });

    return new FormGroup<ApiKeyFormGroupContent>({
      id: new FormControl(
        { value: apiKeyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      keyPrefix: new FormControl(apiKeyRawValue.keyPrefix, {
        validators: [Validators.required],
      }),
      keyHash: new FormControl(apiKeyRawValue.keyHash, {
        validators: [Validators.required],
      }),
      environment: new FormControl(apiKeyRawValue.environment, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(apiKeyRawValue.isActive, {
        validators: [Validators.required],
      }),
      issuedAt: new FormControl(apiKeyRawValue.issuedAt, {
        validators: [Validators.required],
      }),
      revokedAt: new FormControl(apiKeyRawValue.revokedAt),
      graceExpiresAt: new FormControl(apiKeyRawValue.graceExpiresAt),
      tenant: new FormControl(apiKeyRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getApiKey(form: ApiKeyFormGroup): IApiKey | NewApiKey {
    return this.convertApiKeyRawValueToApiKey(form.getRawValue());
  }

  resetForm(form: ApiKeyFormGroup, apiKey: ApiKeyFormGroupInput): void {
    const apiKeyRawValue = this.convertApiKeyToApiKeyRawValue({ ...this.getFormDefaults(), ...apiKey });
    form.reset({
      ...apiKeyRawValue,
      id: { value: apiKeyRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ApiKeyFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isActive: false,
      issuedAt: currentTime,
      revokedAt: currentTime,
      graceExpiresAt: currentTime,
    };
  }

  private convertApiKeyRawValueToApiKey(rawApiKey: ApiKeyFormRawValue | NewApiKeyFormRawValue): IApiKey | NewApiKey {
    return {
      ...rawApiKey,
      issuedAt: dayjs(rawApiKey.issuedAt, DATE_TIME_FORMAT),
      revokedAt: dayjs(rawApiKey.revokedAt, DATE_TIME_FORMAT),
      graceExpiresAt: dayjs(rawApiKey.graceExpiresAt, DATE_TIME_FORMAT),
    };
  }

  private convertApiKeyToApiKeyRawValue(
    apiKey: IApiKey | (Partial<NewApiKey> & ApiKeyFormDefaults),
  ): ApiKeyFormRawValue | PartialWithRequiredKeyOf<NewApiKeyFormRawValue> {
    return {
      ...apiKey,
      issuedAt: apiKey.issuedAt ? apiKey.issuedAt.format(DATE_TIME_FORMAT) : undefined,
      revokedAt: apiKey.revokedAt ? apiKey.revokedAt.format(DATE_TIME_FORMAT) : undefined,
      graceExpiresAt: apiKey.graceExpiresAt ? apiKey.graceExpiresAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
