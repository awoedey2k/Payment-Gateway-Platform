import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPaymentMethod, NewPaymentMethod } from '../payment-method.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaymentMethod for edit and NewPaymentMethodFormGroupInput for create.
 */
type PaymentMethodFormGroupInput = IPaymentMethod | PartialWithRequiredKeyOf<NewPaymentMethod>;

type PaymentMethodFormDefaults = Pick<NewPaymentMethod, 'id'>;

type PaymentMethodFormGroupContent = {
  id: FormControl<IPaymentMethod['id'] | NewPaymentMethod['id']>;
  code: FormControl<IPaymentMethod['code']>;
  displayName: FormControl<IPaymentMethod['displayName']>;
  category: FormControl<IPaymentMethod['category']>;
};

export type PaymentMethodFormGroup = FormGroup<PaymentMethodFormGroupContent>;

@Service()
export class PaymentMethodFormService {
  createPaymentMethodFormGroup(paymentMethod?: PaymentMethodFormGroupInput): PaymentMethodFormGroup {
    const paymentMethodRawValue = {
      ...this.getFormDefaults(),
      ...(paymentMethod ?? { id: null }),
    };

    return new FormGroup<PaymentMethodFormGroupContent>({
      id: new FormControl(
        { value: paymentMethodRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(paymentMethodRawValue.code, {
        validators: [Validators.required],
      }),
      displayName: new FormControl(paymentMethodRawValue.displayName, {
        validators: [Validators.required],
      }),
      category: new FormControl(paymentMethodRawValue.category, {
        validators: [Validators.required],
      }),
    });
  }

  getPaymentMethod(form: PaymentMethodFormGroup): IPaymentMethod | NewPaymentMethod {
    return form.getRawValue();
  }

  resetForm(form: PaymentMethodFormGroup, paymentMethod: PaymentMethodFormGroupInput): void {
    const paymentMethodRawValue = { ...this.getFormDefaults(), ...paymentMethod };
    form.reset({
      ...paymentMethodRawValue,
      id: { value: paymentMethodRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PaymentMethodFormDefaults {
    return {
      id: null,
    };
  }
}
