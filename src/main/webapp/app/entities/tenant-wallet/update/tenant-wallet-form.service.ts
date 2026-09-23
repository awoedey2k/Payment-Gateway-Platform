import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITenantWallet, NewTenantWallet } from '../tenant-wallet.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenantWallet for edit and NewTenantWalletFormGroupInput for create.
 */
type TenantWalletFormGroupInput = ITenantWallet | PartialWithRequiredKeyOf<NewTenantWallet>;

type TenantWalletFormDefaults = Pick<NewTenantWallet, 'id'>;

type TenantWalletFormGroupContent = {
  id: FormControl<ITenantWallet['id'] | NewTenantWallet['id']>;
  currencyCode: FormControl<ITenantWallet['currencyCode']>;
  availableBalance: FormControl<ITenantWallet['availableBalance']>;
  lockedBalance: FormControl<ITenantWallet['lockedBalance']>;
  tenant: FormControl<ITenantWallet['tenant']>;
};

export type TenantWalletFormGroup = FormGroup<TenantWalletFormGroupContent>;

@Service()
export class TenantWalletFormService {
  createTenantWalletFormGroup(tenantWallet?: TenantWalletFormGroupInput): TenantWalletFormGroup {
    const tenantWalletRawValue = {
      ...this.getFormDefaults(),
      ...(tenantWallet ?? { id: null }),
    };

    return new FormGroup<TenantWalletFormGroupContent>({
      id: new FormControl(
        { value: tenantWalletRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      currencyCode: new FormControl(tenantWalletRawValue.currencyCode, {
        validators: [Validators.required],
      }),
      availableBalance: new FormControl(tenantWalletRawValue.availableBalance, {
        validators: [Validators.required],
      }),
      lockedBalance: new FormControl(tenantWalletRawValue.lockedBalance, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(tenantWalletRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getTenantWallet(form: TenantWalletFormGroup): ITenantWallet | NewTenantWallet {
    return form.getRawValue();
  }

  resetForm(form: TenantWalletFormGroup, tenantWallet: TenantWalletFormGroupInput): void {
    const tenantWalletRawValue = { ...this.getFormDefaults(), ...tenantWallet };
    form.reset({
      ...tenantWalletRawValue,
      id: { value: tenantWalletRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TenantWalletFormDefaults {
    return {
      id: null,
    };
  }
}
