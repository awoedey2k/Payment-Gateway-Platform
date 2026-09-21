import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPayoutSchedule, NewPayoutSchedule } from '../payout-schedule.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPayoutSchedule for edit and NewPayoutScheduleFormGroupInput for create.
 */
type PayoutScheduleFormGroupInput = IPayoutSchedule | PartialWithRequiredKeyOf<NewPayoutSchedule>;

type PayoutScheduleFormDefaults = Pick<NewPayoutSchedule, 'id' | 'isActive'>;

type PayoutScheduleFormGroupContent = {
  id: FormControl<IPayoutSchedule['id'] | NewPayoutSchedule['id']>;
  frequencyMode: FormControl<IPayoutSchedule['frequencyMode']>;
  thresholdAmount: FormControl<IPayoutSchedule['thresholdAmount']>;
  isActive: FormControl<IPayoutSchedule['isActive']>;
  tenant: FormControl<IPayoutSchedule['tenant']>;
};

export type PayoutScheduleFormGroup = FormGroup<PayoutScheduleFormGroupContent>;

@Service()
export class PayoutScheduleFormService {
  createPayoutScheduleFormGroup(payoutSchedule?: PayoutScheduleFormGroupInput): PayoutScheduleFormGroup {
    const payoutScheduleRawValue = {
      ...this.getFormDefaults(),
      ...(payoutSchedule ?? { id: null }),
    };

    return new FormGroup<PayoutScheduleFormGroupContent>({
      id: new FormControl(
        { value: payoutScheduleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      frequencyMode: new FormControl(payoutScheduleRawValue.frequencyMode, {
        validators: [Validators.required],
      }),
      thresholdAmount: new FormControl(payoutScheduleRawValue.thresholdAmount, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(payoutScheduleRawValue.isActive, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(payoutScheduleRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getPayoutSchedule(form: PayoutScheduleFormGroup): IPayoutSchedule | NewPayoutSchedule {
    return form.getRawValue();
  }

  resetForm(form: PayoutScheduleFormGroup, payoutSchedule: PayoutScheduleFormGroupInput): void {
    const payoutScheduleRawValue = { ...this.getFormDefaults(), ...payoutSchedule };
    form.reset({
      ...payoutScheduleRawValue,
      id: { value: payoutScheduleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PayoutScheduleFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
