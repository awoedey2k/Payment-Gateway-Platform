import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILedgerAccount, NewLedgerAccount } from '../ledger-account.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILedgerAccount for edit and NewLedgerAccountFormGroupInput for create.
 */
type LedgerAccountFormGroupInput = ILedgerAccount | PartialWithRequiredKeyOf<NewLedgerAccount>;

type LedgerAccountFormDefaults = Pick<NewLedgerAccount, 'id'>;

type LedgerAccountFormGroupContent = {
  id: FormControl<ILedgerAccount['id'] | NewLedgerAccount['id']>;
  accountCode: FormControl<ILedgerAccount['accountCode']>;
  accountType: FormControl<ILedgerAccount['accountType']>;
  currencyCode: FormControl<ILedgerAccount['currencyCode']>;
};

export type LedgerAccountFormGroup = FormGroup<LedgerAccountFormGroupContent>;

@Service()
export class LedgerAccountFormService {
  createLedgerAccountFormGroup(ledgerAccount?: LedgerAccountFormGroupInput): LedgerAccountFormGroup {
    const ledgerAccountRawValue = {
      ...this.getFormDefaults(),
      ...(ledgerAccount ?? { id: null }),
    };

    return new FormGroup<LedgerAccountFormGroupContent>({
      id: new FormControl(
        { value: ledgerAccountRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      accountCode: new FormControl(ledgerAccountRawValue.accountCode, {
        validators: [Validators.required],
      }),
      accountType: new FormControl(ledgerAccountRawValue.accountType, {
        validators: [Validators.required],
      }),
      currencyCode: new FormControl(ledgerAccountRawValue.currencyCode, {
        validators: [Validators.required],
      }),
    });
  }

  getLedgerAccount(form: LedgerAccountFormGroup): ILedgerAccount | NewLedgerAccount {
    return form.getRawValue();
  }

  resetForm(form: LedgerAccountFormGroup, ledgerAccount: LedgerAccountFormGroupInput): void {
    const ledgerAccountRawValue = { ...this.getFormDefaults(), ...ledgerAccount };
    form.reset({
      ...ledgerAccountRawValue,
      id: { value: ledgerAccountRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LedgerAccountFormDefaults {
    return {
      id: null,
    };
  }
}
