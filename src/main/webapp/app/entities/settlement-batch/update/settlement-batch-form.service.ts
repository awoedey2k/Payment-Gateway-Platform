import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { ISettlementBatch, NewSettlementBatch } from '../settlement-batch.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISettlementBatch for edit and NewSettlementBatchFormGroupInput for create.
 */
type SettlementBatchFormGroupInput = ISettlementBatch | PartialWithRequiredKeyOf<NewSettlementBatch>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISettlementBatch | NewSettlementBatch> = Omit<T, 'scheduledAt' | 'completedAt'> & {
  scheduledAt?: string | null;
  completedAt?: string | null;
};

type SettlementBatchFormRawValue = FormValueOf<ISettlementBatch>;

type NewSettlementBatchFormRawValue = FormValueOf<NewSettlementBatch>;

type SettlementBatchFormDefaults = Pick<NewSettlementBatch, 'id' | 'scheduledAt' | 'completedAt'>;

type SettlementBatchFormGroupContent = {
  id: FormControl<SettlementBatchFormRawValue['id'] | NewSettlementBatch['id']>;
  reference: FormControl<SettlementBatchFormRawValue['reference']>;
  status: FormControl<SettlementBatchFormRawValue['status']>;
  totalAmount: FormControl<SettlementBatchFormRawValue['totalAmount']>;
  currencyCode: FormControl<SettlementBatchFormRawValue['currencyCode']>;
  scheduledAt: FormControl<SettlementBatchFormRawValue['scheduledAt']>;
  completedAt: FormControl<SettlementBatchFormRawValue['completedAt']>;
  tenant: FormControl<SettlementBatchFormRawValue['tenant']>;
};

export type SettlementBatchFormGroup = FormGroup<SettlementBatchFormGroupContent>;

@Service()
export class SettlementBatchFormService {
  createSettlementBatchFormGroup(settlementBatch?: SettlementBatchFormGroupInput): SettlementBatchFormGroup {
    const settlementBatchRawValue = this.convertSettlementBatchToSettlementBatchRawValue({
      ...this.getFormDefaults(),
      ...(settlementBatch ?? { id: null }),
    });

    return new FormGroup<SettlementBatchFormGroupContent>({
      id: new FormControl(
        { value: settlementBatchRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(settlementBatchRawValue.reference, {
        validators: [Validators.required],
      }),
      status: new FormControl(settlementBatchRawValue.status, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(settlementBatchRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      currencyCode: new FormControl(settlementBatchRawValue.currencyCode, {
        validators: [Validators.required],
      }),
      scheduledAt: new FormControl(settlementBatchRawValue.scheduledAt, {
        validators: [Validators.required],
      }),
      completedAt: new FormControl(settlementBatchRawValue.completedAt),
      tenant: new FormControl(settlementBatchRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getSettlementBatch(form: SettlementBatchFormGroup): ISettlementBatch | NewSettlementBatch {
    return this.convertSettlementBatchRawValueToSettlementBatch(form.getRawValue());
  }

  resetForm(form: SettlementBatchFormGroup, settlementBatch: SettlementBatchFormGroupInput): void {
    const settlementBatchRawValue = this.convertSettlementBatchToSettlementBatchRawValue({ ...this.getFormDefaults(), ...settlementBatch });
    form.reset({
      ...settlementBatchRawValue,
      id: { value: settlementBatchRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SettlementBatchFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      scheduledAt: currentTime,
      completedAt: currentTime,
    };
  }

  private convertSettlementBatchRawValueToSettlementBatch(
    rawSettlementBatch: SettlementBatchFormRawValue | NewSettlementBatchFormRawValue,
  ): ISettlementBatch | NewSettlementBatch {
    return {
      ...rawSettlementBatch,
      scheduledAt: dayjs(rawSettlementBatch.scheduledAt, DATE_TIME_FORMAT),
      completedAt: dayjs(rawSettlementBatch.completedAt, DATE_TIME_FORMAT),
    };
  }

  private convertSettlementBatchToSettlementBatchRawValue(
    settlementBatch: ISettlementBatch | (Partial<NewSettlementBatch> & SettlementBatchFormDefaults),
  ): SettlementBatchFormRawValue | PartialWithRequiredKeyOf<NewSettlementBatchFormRawValue> {
    return {
      ...settlementBatch,
      scheduledAt: settlementBatch.scheduledAt ? settlementBatch.scheduledAt.format(DATE_TIME_FORMAT) : undefined,
      completedAt: settlementBatch.completedAt ? settlementBatch.completedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
