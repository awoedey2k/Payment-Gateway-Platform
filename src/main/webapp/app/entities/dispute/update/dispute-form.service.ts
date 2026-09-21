import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IDispute, NewDispute } from '../dispute.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDispute for edit and NewDisputeFormGroupInput for create.
 */
type DisputeFormGroupInput = IDispute | PartialWithRequiredKeyOf<NewDispute>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDispute | NewDispute> = Omit<T, 'dueDate' | 'evidenceSubmittedAt' | 'resolvedAt'> & {
  dueDate?: string | null;
  evidenceSubmittedAt?: string | null;
  resolvedAt?: string | null;
};

type DisputeFormRawValue = FormValueOf<IDispute>;

type NewDisputeFormRawValue = FormValueOf<NewDispute>;

type DisputeFormDefaults = Pick<NewDispute, 'id' | 'dueDate' | 'evidenceSubmittedAt' | 'resolvedAt'>;

type DisputeFormGroupContent = {
  id: FormControl<DisputeFormRawValue['id'] | NewDispute['id']>;
  caseReference: FormControl<DisputeFormRawValue['caseReference']>;
  amount: FormControl<DisputeFormRawValue['amount']>;
  currencyCode: FormControl<DisputeFormRawValue['currencyCode']>;
  reasonCode: FormControl<DisputeFormRawValue['reasonCode']>;
  reasonDescription: FormControl<DisputeFormRawValue['reasonDescription']>;
  status: FormControl<DisputeFormRawValue['status']>;
  dueDate: FormControl<DisputeFormRawValue['dueDate']>;
  evidenceSubmittedAt: FormControl<DisputeFormRawValue['evidenceSubmittedAt']>;
  resolvedAt: FormControl<DisputeFormRawValue['resolvedAt']>;
  tenant: FormControl<DisputeFormRawValue['tenant']>;
  transaction: FormControl<DisputeFormRawValue['transaction']>;
};

export type DisputeFormGroup = FormGroup<DisputeFormGroupContent>;

@Service()
export class DisputeFormService {
  createDisputeFormGroup(dispute?: DisputeFormGroupInput): DisputeFormGroup {
    const disputeRawValue = this.convertDisputeToDisputeRawValue({
      ...this.getFormDefaults(),
      ...(dispute ?? { id: null }),
    });

    return new FormGroup<DisputeFormGroupContent>({
      id: new FormControl(
        { value: disputeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      caseReference: new FormControl(disputeRawValue.caseReference, {
        validators: [Validators.required],
      }),
      amount: new FormControl(disputeRawValue.amount, {
        validators: [Validators.required],
      }),
      currencyCode: new FormControl(disputeRawValue.currencyCode, {
        validators: [Validators.required],
      }),
      reasonCode: new FormControl(disputeRawValue.reasonCode, {
        validators: [Validators.required],
      }),
      reasonDescription: new FormControl(disputeRawValue.reasonDescription),
      status: new FormControl(disputeRawValue.status, {
        validators: [Validators.required],
      }),
      dueDate: new FormControl(disputeRawValue.dueDate, {
        validators: [Validators.required],
      }),
      evidenceSubmittedAt: new FormControl(disputeRawValue.evidenceSubmittedAt),
      resolvedAt: new FormControl(disputeRawValue.resolvedAt),
      tenant: new FormControl(disputeRawValue.tenant, {
        validators: [Validators.required],
      }),
      transaction: new FormControl(disputeRawValue.transaction, {
        validators: [Validators.required],
      }),
    });
  }

  getDispute(form: DisputeFormGroup): IDispute | NewDispute {
    return this.convertDisputeRawValueToDispute(form.getRawValue());
  }

  resetForm(form: DisputeFormGroup, dispute: DisputeFormGroupInput): void {
    const disputeRawValue = this.convertDisputeToDisputeRawValue({ ...this.getFormDefaults(), ...dispute });
    form.reset({
      ...disputeRawValue,
      id: { value: disputeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DisputeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dueDate: currentTime,
      evidenceSubmittedAt: currentTime,
      resolvedAt: currentTime,
    };
  }

  private convertDisputeRawValueToDispute(rawDispute: DisputeFormRawValue | NewDisputeFormRawValue): IDispute | NewDispute {
    return {
      ...rawDispute,
      dueDate: dayjs(rawDispute.dueDate, DATE_TIME_FORMAT),
      evidenceSubmittedAt: dayjs(rawDispute.evidenceSubmittedAt, DATE_TIME_FORMAT),
      resolvedAt: dayjs(rawDispute.resolvedAt, DATE_TIME_FORMAT),
    };
  }

  private convertDisputeToDisputeRawValue(
    dispute: IDispute | (Partial<NewDispute> & DisputeFormDefaults),
  ): DisputeFormRawValue | PartialWithRequiredKeyOf<NewDisputeFormRawValue> {
    return {
      ...dispute,
      dueDate: dispute.dueDate ? dispute.dueDate.format(DATE_TIME_FORMAT) : undefined,
      evidenceSubmittedAt: dispute.evidenceSubmittedAt ? dispute.evidenceSubmittedAt.format(DATE_TIME_FORMAT) : undefined,
      resolvedAt: dispute.resolvedAt ? dispute.resolvedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
