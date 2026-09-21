import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICurrency, NewCurrency } from '../currency.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICurrency for edit and NewCurrencyFormGroupInput for create.
 */
type CurrencyFormGroupInput = ICurrency | PartialWithRequiredKeyOf<NewCurrency>;

type CurrencyFormDefaults = Pick<NewCurrency, 'id'>;

type CurrencyFormGroupContent = {
  id: FormControl<ICurrency['id'] | NewCurrency['id']>;
  code: FormControl<ICurrency['code']>;
  name: FormControl<ICurrency['name']>;
  minorUnit: FormControl<ICurrency['minorUnit']>;
};

export type CurrencyFormGroup = FormGroup<CurrencyFormGroupContent>;

@Service()
export class CurrencyFormService {
  createCurrencyFormGroup(currency?: CurrencyFormGroupInput): CurrencyFormGroup {
    const currencyRawValue = {
      ...this.getFormDefaults(),
      ...(currency ?? { id: null }),
    };

    return new FormGroup<CurrencyFormGroupContent>({
      id: new FormControl(
        { value: currencyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(currencyRawValue.code, {
        validators: [Validators.required, Validators.maxLength(3)],
      }),
      name: new FormControl(currencyRawValue.name, {
        validators: [Validators.required],
      }),
      minorUnit: new FormControl(currencyRawValue.minorUnit, {
        validators: [Validators.required],
      }),
    });
  }

  getCurrency(form: CurrencyFormGroup): ICurrency | NewCurrency {
    return form.getRawValue();
  }

  resetForm(form: CurrencyFormGroup, currency: CurrencyFormGroupInput): void {
    const currencyRawValue = { ...this.getFormDefaults(), ...currency };
    form.reset({
      ...currencyRawValue,
      id: { value: currencyRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CurrencyFormDefaults {
    return {
      id: null,
    };
  }
}
