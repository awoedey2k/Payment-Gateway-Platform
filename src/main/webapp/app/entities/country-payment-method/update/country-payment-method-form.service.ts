import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICountryPaymentMethod, NewCountryPaymentMethod } from '../country-payment-method.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICountryPaymentMethod for edit and NewCountryPaymentMethodFormGroupInput for create.
 */
type CountryPaymentMethodFormGroupInput = ICountryPaymentMethod | PartialWithRequiredKeyOf<NewCountryPaymentMethod>;

type CountryPaymentMethodFormDefaults = Pick<NewCountryPaymentMethod, 'id' | 'supportsRecurring' | 'supportsInstantRefund' | 'isActive'>;

type CountryPaymentMethodFormGroupContent = {
  id: FormControl<ICountryPaymentMethod['id'] | NewCountryPaymentMethod['id']>;
  minTxnAmount: FormControl<ICountryPaymentMethod['minTxnAmount']>;
  maxTxnAmount: FormControl<ICountryPaymentMethod['maxTxnAmount']>;
  supportsRecurring: FormControl<ICountryPaymentMethod['supportsRecurring']>;
  supportsInstantRefund: FormControl<ICountryPaymentMethod['supportsInstantRefund']>;
  isActive: FormControl<ICountryPaymentMethod['isActive']>;
  country: FormControl<ICountryPaymentMethod['country']>;
  paymentMethod: FormControl<ICountryPaymentMethod['paymentMethod']>;
};

export type CountryPaymentMethodFormGroup = FormGroup<CountryPaymentMethodFormGroupContent>;

@Service()
export class CountryPaymentMethodFormService {
  createCountryPaymentMethodFormGroup(countryPaymentMethod?: CountryPaymentMethodFormGroupInput): CountryPaymentMethodFormGroup {
    const countryPaymentMethodRawValue = {
      ...this.getFormDefaults(),
      ...(countryPaymentMethod ?? { id: null }),
    };

    return new FormGroup<CountryPaymentMethodFormGroupContent>({
      id: new FormControl(
        { value: countryPaymentMethodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      minTxnAmount: new FormControl(countryPaymentMethodRawValue.minTxnAmount, {
        validators: [Validators.required],
      }),
      maxTxnAmount: new FormControl(countryPaymentMethodRawValue.maxTxnAmount, {
        validators: [Validators.required],
      }),
      supportsRecurring: new FormControl(countryPaymentMethodRawValue.supportsRecurring, {
        validators: [Validators.required],
      }),
      supportsInstantRefund: new FormControl(countryPaymentMethodRawValue.supportsInstantRefund, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(countryPaymentMethodRawValue.isActive, {
        validators: [Validators.required],
      }),
      country: new FormControl(countryPaymentMethodRawValue.country, {
        validators: [Validators.required],
      }),
      paymentMethod: new FormControl(countryPaymentMethodRawValue.paymentMethod, {
        validators: [Validators.required],
      }),
    });
  }

  getCountryPaymentMethod(form: CountryPaymentMethodFormGroup): ICountryPaymentMethod | NewCountryPaymentMethod {
    return form.getRawValue();
  }

  resetForm(form: CountryPaymentMethodFormGroup, countryPaymentMethod: CountryPaymentMethodFormGroupInput): void {
    const countryPaymentMethodRawValue = { ...this.getFormDefaults(), ...countryPaymentMethod };
    form.reset({
      ...countryPaymentMethodRawValue,
      id: { value: countryPaymentMethodRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CountryPaymentMethodFormDefaults {
    return {
      id: null,
      supportsRecurring: false,
      supportsInstantRefund: false,
      isActive: false,
    };
  }
}
