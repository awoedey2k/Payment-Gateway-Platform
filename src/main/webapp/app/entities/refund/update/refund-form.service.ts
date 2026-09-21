import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IRefund, NewRefund } from '../refund.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRefund for edit and NewRefundFormGroupInput for create.
 */
type RefundFormGroupInput = IRefund | PartialWithRequiredKeyOf<NewRefund>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRefund | NewRefund> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type RefundFormRawValue = FormValueOf<IRefund>;

type NewRefundFormRawValue = FormValueOf<NewRefund>;

type RefundFormDefaults = Pick<NewRefund, 'id' | 'createdAt'>;

type RefundFormGroupContent = {
  id: FormControl<RefundFormRawValue['id'] | NewRefund['id']>;
  reference: FormControl<RefundFormRawValue['reference']>;
  amount: FormControl<RefundFormRawValue['amount']>;
  reason: FormControl<RefundFormRawValue['reason']>;
  status: FormControl<RefundFormRawValue['status']>;
  createdAt: FormControl<RefundFormRawValue['createdAt']>;
  transaction: FormControl<RefundFormRawValue['transaction']>;
};

export type RefundFormGroup = FormGroup<RefundFormGroupContent>;

@Service()
export class RefundFormService {
  createRefundFormGroup(refund?: RefundFormGroupInput): RefundFormGroup {
    const refundRawValue = this.convertRefundToRefundRawValue({
      ...this.getFormDefaults(),
      ...(refund ?? { id: null }),
    });

    return new FormGroup<RefundFormGroupContent>({
      id: new FormControl(
        { value: refundRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(refundRawValue.reference, {
        validators: [Validators.required],
      }),
      amount: new FormControl(refundRawValue.amount, {
        validators: [Validators.required],
      }),
      reason: new FormControl(refundRawValue.reason, {
        validators: [Validators.required],
      }),
      status: new FormControl(refundRawValue.status, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(refundRawValue.createdAt, {
        validators: [Validators.required],
      }),
      transaction: new FormControl(refundRawValue.transaction, {
        validators: [Validators.required],
      }),
    });
  }

  getRefund(form: RefundFormGroup): IRefund | NewRefund {
    return this.convertRefundRawValueToRefund(form.getRawValue());
  }

  resetForm(form: RefundFormGroup, refund: RefundFormGroupInput): void {
    const refundRawValue = this.convertRefundToRefundRawValue({ ...this.getFormDefaults(), ...refund });
    form.reset({
      ...refundRawValue,
      id: { value: refundRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RefundFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertRefundRawValueToRefund(rawRefund: RefundFormRawValue | NewRefundFormRawValue): IRefund | NewRefund {
    return {
      ...rawRefund,
      createdAt: dayjs(rawRefund.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertRefundToRefundRawValue(
    refund: IRefund | (Partial<NewRefund> & RefundFormDefaults),
  ): RefundFormRawValue | PartialWithRequiredKeyOf<NewRefundFormRawValue> {
    return {
      ...refund,
      createdAt: refund.createdAt ? refund.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
