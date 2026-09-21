import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { ITransaction, NewTransaction } from '../transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransaction for edit and NewTransactionFormGroupInput for create.
 */
type TransactionFormGroupInput = ITransaction | PartialWithRequiredKeyOf<NewTransaction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITransaction | NewTransaction> = Omit<T, 'createdAt' | 'completedAt'> & {
  createdAt?: string | null;
  completedAt?: string | null;
};

type TransactionFormRawValue = FormValueOf<ITransaction>;

type NewTransactionFormRawValue = FormValueOf<NewTransaction>;

type TransactionFormDefaults = Pick<NewTransaction, 'id' | 'createdAt' | 'completedAt'>;

type TransactionFormGroupContent = {
  id: FormControl<TransactionFormRawValue['id'] | NewTransaction['id']>;
  reference: FormControl<TransactionFormRawValue['reference']>;
  tenantReference: FormControl<TransactionFormRawValue['tenantReference']>;
  status: FormControl<TransactionFormRawValue['status']>;
  amount: FormControl<TransactionFormRawValue['amount']>;
  feeAmount: FormControl<TransactionFormRawValue['feeAmount']>;
  netAmount: FormControl<TransactionFormRawValue['netAmount']>;
  currencyCode: FormControl<TransactionFormRawValue['currencyCode']>;
  countryCode: FormControl<TransactionFormRawValue['countryCode']>;
  paymentMethodCode: FormControl<TransactionFormRawValue['paymentMethodCode']>;
  idempotencyKey: FormControl<TransactionFormRawValue['idempotencyKey']>;
  customerEmail: FormControl<TransactionFormRawValue['customerEmail']>;
  customerPhone: FormControl<TransactionFormRawValue['customerPhone']>;
  createdAt: FormControl<TransactionFormRawValue['createdAt']>;
  completedAt: FormControl<TransactionFormRawValue['completedAt']>;
  tenant: FormControl<TransactionFormRawValue['tenant']>;
};

export type TransactionFormGroup = FormGroup<TransactionFormGroupContent>;

@Service()
export class TransactionFormService {
  createTransactionFormGroup(transaction?: TransactionFormGroupInput): TransactionFormGroup {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({
      ...this.getFormDefaults(),
      ...(transaction ?? { id: null }),
    });

    return new FormGroup<TransactionFormGroupContent>({
      id: new FormControl(
        { value: transactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(transactionRawValue.reference, {
        validators: [Validators.required],
      }),
      tenantReference: new FormControl(transactionRawValue.tenantReference),
      status: new FormControl(transactionRawValue.status, {
        validators: [Validators.required],
      }),
      amount: new FormControl(transactionRawValue.amount, {
        validators: [Validators.required],
      }),
      feeAmount: new FormControl(transactionRawValue.feeAmount, {
        validators: [Validators.required],
      }),
      netAmount: new FormControl(transactionRawValue.netAmount, {
        validators: [Validators.required],
      }),
      currencyCode: new FormControl(transactionRawValue.currencyCode, {
        validators: [Validators.required],
      }),
      countryCode: new FormControl(transactionRawValue.countryCode, {
        validators: [Validators.required],
      }),
      paymentMethodCode: new FormControl(transactionRawValue.paymentMethodCode, {
        validators: [Validators.required],
      }),
      idempotencyKey: new FormControl(transactionRawValue.idempotencyKey, {
        validators: [Validators.required],
      }),
      customerEmail: new FormControl(transactionRawValue.customerEmail),
      customerPhone: new FormControl(transactionRawValue.customerPhone),
      createdAt: new FormControl(transactionRawValue.createdAt, {
        validators: [Validators.required],
      }),
      completedAt: new FormControl(transactionRawValue.completedAt),
      tenant: new FormControl(transactionRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getTransaction(form: TransactionFormGroup): ITransaction | NewTransaction {
    return this.convertTransactionRawValueToTransaction(form.getRawValue());
  }

  resetForm(form: TransactionFormGroup, transaction: TransactionFormGroupInput): void {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({ ...this.getFormDefaults(), ...transaction });
    form.reset({
      ...transactionRawValue,
      id: { value: transactionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TransactionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      completedAt: currentTime,
    };
  }

  private convertTransactionRawValueToTransaction(
    rawTransaction: TransactionFormRawValue | NewTransactionFormRawValue,
  ): ITransaction | NewTransaction {
    return {
      ...rawTransaction,
      createdAt: dayjs(rawTransaction.createdAt, DATE_TIME_FORMAT),
      completedAt: dayjs(rawTransaction.completedAt, DATE_TIME_FORMAT),
    };
  }

  private convertTransactionToTransactionRawValue(
    transaction: ITransaction | (Partial<NewTransaction> & TransactionFormDefaults),
  ): TransactionFormRawValue | PartialWithRequiredKeyOf<NewTransactionFormRawValue> {
    return {
      ...transaction,
      createdAt: transaction.createdAt ? transaction.createdAt.format(DATE_TIME_FORMAT) : undefined,
      completedAt: transaction.completedAt ? transaction.completedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
