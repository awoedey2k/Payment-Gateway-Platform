import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { ICorporateTenant, NewCorporateTenant } from '../corporate-tenant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICorporateTenant for edit and NewCorporateTenantFormGroupInput for create.
 */
type CorporateTenantFormGroupInput = ICorporateTenant | PartialWithRequiredKeyOf<NewCorporateTenant>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICorporateTenant | NewCorporateTenant> = Omit<T, 'createdAt' | 'activatedAt'> & {
  createdAt?: string | null;
  activatedAt?: string | null;
};

type CorporateTenantFormRawValue = FormValueOf<ICorporateTenant>;

type NewCorporateTenantFormRawValue = FormValueOf<NewCorporateTenant>;

type CorporateTenantFormDefaults = Pick<NewCorporateTenant, 'id' | 'createdAt' | 'activatedAt'>;

type CorporateTenantFormGroupContent = {
  id: FormControl<CorporateTenantFormRawValue['id'] | NewCorporateTenant['id']>;
  legalBusinessName: FormControl<CorporateTenantFormRawValue['legalBusinessName']>;
  businessRegistrationNumber: FormControl<CorporateTenantFormRawValue['businessRegistrationNumber']>;
  taxIdentificationNumber: FormControl<CorporateTenantFormRawValue['taxIdentificationNumber']>;
  operatingJurisdiction: FormControl<CorporateTenantFormRawValue['operatingJurisdiction']>;
  status: FormControl<CorporateTenantFormRawValue['status']>;
  kycStatus: FormControl<CorporateTenantFormRawValue['kycStatus']>;
  riskScore: FormControl<CorporateTenantFormRawValue['riskScore']>;
  createdAt: FormControl<CorporateTenantFormRawValue['createdAt']>;
  activatedAt: FormControl<CorporateTenantFormRawValue['activatedAt']>;
};

export type CorporateTenantFormGroup = FormGroup<CorporateTenantFormGroupContent>;

@Service()
export class CorporateTenantFormService {
  createCorporateTenantFormGroup(corporateTenant?: CorporateTenantFormGroupInput): CorporateTenantFormGroup {
    const corporateTenantRawValue = this.convertCorporateTenantToCorporateTenantRawValue({
      ...this.getFormDefaults(),
      ...(corporateTenant ?? { id: null }),
    });

    return new FormGroup<CorporateTenantFormGroupContent>({
      id: new FormControl(
        { value: corporateTenantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      legalBusinessName: new FormControl(corporateTenantRawValue.legalBusinessName, {
        validators: [Validators.required],
      }),
      businessRegistrationNumber: new FormControl(corporateTenantRawValue.businessRegistrationNumber, {
        validators: [Validators.required],
      }),
      taxIdentificationNumber: new FormControl(corporateTenantRawValue.taxIdentificationNumber, {
        validators: [Validators.required],
      }),
      operatingJurisdiction: new FormControl(corporateTenantRawValue.operatingJurisdiction, {
        validators: [Validators.required],
      }),
      status: new FormControl(corporateTenantRawValue.status, {
        validators: [Validators.required],
      }),
      kycStatus: new FormControl(corporateTenantRawValue.kycStatus, {
        validators: [Validators.required],
      }),
      riskScore: new FormControl(corporateTenantRawValue.riskScore),
      createdAt: new FormControl(corporateTenantRawValue.createdAt, {
        validators: [Validators.required],
      }),
      activatedAt: new FormControl(corporateTenantRawValue.activatedAt),
    });
  }

  getCorporateTenant(form: CorporateTenantFormGroup): ICorporateTenant | NewCorporateTenant {
    return this.convertCorporateTenantRawValueToCorporateTenant(form.getRawValue());
  }

  resetForm(form: CorporateTenantFormGroup, corporateTenant: CorporateTenantFormGroupInput): void {
    const corporateTenantRawValue = this.convertCorporateTenantToCorporateTenantRawValue({ ...this.getFormDefaults(), ...corporateTenant });
    form.reset({
      ...corporateTenantRawValue,
      id: { value: corporateTenantRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CorporateTenantFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      activatedAt: currentTime,
    };
  }

  private convertCorporateTenantRawValueToCorporateTenant(
    rawCorporateTenant: CorporateTenantFormRawValue | NewCorporateTenantFormRawValue,
  ): ICorporateTenant | NewCorporateTenant {
    return {
      ...rawCorporateTenant,
      createdAt: dayjs(rawCorporateTenant.createdAt, DATE_TIME_FORMAT),
      activatedAt: dayjs(rawCorporateTenant.activatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCorporateTenantToCorporateTenantRawValue(
    corporateTenant: ICorporateTenant | (Partial<NewCorporateTenant> & CorporateTenantFormDefaults),
  ): CorporateTenantFormRawValue | PartialWithRequiredKeyOf<NewCorporateTenantFormRawValue> {
    return {
      ...corporateTenant,
      createdAt: corporateTenant.createdAt ? corporateTenant.createdAt.format(DATE_TIME_FORMAT) : undefined,
      activatedAt: corporateTenant.activatedAt ? corporateTenant.activatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
