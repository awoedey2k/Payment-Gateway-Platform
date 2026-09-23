import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IForexRate, NewForexRate } from '../forex-rate.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IForexRate for edit and NewForexRateFormGroupInput for create.
 */
type ForexRateFormGroupInput = IForexRate | PartialWithRequiredKeyOf<NewForexRate>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IForexRate | NewForexRate> = Omit<T, 'lockedAt' | 'expiresAt'> & {
  lockedAt?: string | null;
  expiresAt?: string | null;
};

type ForexRateFormRawValue = FormValueOf<IForexRate>;

type NewForexRateFormRawValue = FormValueOf<NewForexRate>;

type ForexRateFormDefaults = Pick<NewForexRate, 'id' | 'lockedAt' | 'expiresAt'>;

type ForexRateFormGroupContent = {
  id: FormControl<ForexRateFormRawValue['id'] | NewForexRate['id']>;
  baseCurrency: FormControl<ForexRateFormRawValue['baseCurrency']>;
  quoteCurrency: FormControl<ForexRateFormRawValue['quoteCurrency']>;
  rate: FormControl<ForexRateFormRawValue['rate']>;
  platformSpreadBps: FormControl<ForexRateFormRawValue['platformSpreadBps']>;
  lockedAt: FormControl<ForexRateFormRawValue['lockedAt']>;
  expiresAt: FormControl<ForexRateFormRawValue['expiresAt']>;
};

export type ForexRateFormGroup = FormGroup<ForexRateFormGroupContent>;

@Service()
export class ForexRateFormService {
  createForexRateFormGroup(forexRate?: ForexRateFormGroupInput): ForexRateFormGroup {
    const forexRateRawValue = this.convertForexRateToForexRateRawValue({
      ...this.getFormDefaults(),
      ...(forexRate ?? { id: null }),
    });

    return new FormGroup<ForexRateFormGroupContent>({
      id: new FormControl(
        { value: forexRateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      baseCurrency: new FormControl(forexRateRawValue.baseCurrency, {
        validators: [Validators.required],
      }),
      quoteCurrency: new FormControl(forexRateRawValue.quoteCurrency, {
        validators: [Validators.required],
      }),
      rate: new FormControl(forexRateRawValue.rate, {
        validators: [Validators.required],
      }),
      platformSpreadBps: new FormControl(forexRateRawValue.platformSpreadBps, {
        validators: [Validators.required],
      }),
      lockedAt: new FormControl(forexRateRawValue.lockedAt, {
        validators: [Validators.required],
      }),
      expiresAt: new FormControl(forexRateRawValue.expiresAt, {
        validators: [Validators.required],
      }),
    });
  }

  getForexRate(form: ForexRateFormGroup): IForexRate | NewForexRate {
    return this.convertForexRateRawValueToForexRate(form.getRawValue());
  }

  resetForm(form: ForexRateFormGroup, forexRate: ForexRateFormGroupInput): void {
    const forexRateRawValue = this.convertForexRateToForexRateRawValue({ ...this.getFormDefaults(), ...forexRate });
    form.reset({
      ...forexRateRawValue,
      id: { value: forexRateRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ForexRateFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lockedAt: currentTime,
      expiresAt: currentTime,
    };
  }

  private convertForexRateRawValueToForexRate(rawForexRate: ForexRateFormRawValue | NewForexRateFormRawValue): IForexRate | NewForexRate {
    return {
      ...rawForexRate,
      lockedAt: dayjs(rawForexRate.lockedAt, DATE_TIME_FORMAT),
      expiresAt: dayjs(rawForexRate.expiresAt, DATE_TIME_FORMAT),
    };
  }

  private convertForexRateToForexRateRawValue(
    forexRate: IForexRate | (Partial<NewForexRate> & ForexRateFormDefaults),
  ): ForexRateFormRawValue | PartialWithRequiredKeyOf<NewForexRateFormRawValue> {
    return {
      ...forexRate,
      lockedAt: forexRate.lockedAt ? forexRate.lockedAt.format(DATE_TIME_FORMAT) : undefined,
      expiresAt: forexRate.expiresAt ? forexRate.expiresAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
