import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITenantFeeConfig, NewTenantFeeConfig } from '../tenant-fee-config.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenantFeeConfig for edit and NewTenantFeeConfigFormGroupInput for create.
 */
type TenantFeeConfigFormGroupInput = ITenantFeeConfig | PartialWithRequiredKeyOf<NewTenantFeeConfig>;

type TenantFeeConfigFormDefaults = Pick<NewTenantFeeConfig, 'id' | 'isActive'>;

type TenantFeeConfigFormGroupContent = {
  id: FormControl<ITenantFeeConfig['id'] | NewTenantFeeConfig['id']>;
  fixedFee: FormControl<ITenantFeeConfig['fixedFee']>;
  percentageFee: FormControl<ITenantFeeConfig['percentageFee']>;
  capAmount: FormControl<ITenantFeeConfig['capAmount']>;
  feeBearer: FormControl<ITenantFeeConfig['feeBearer']>;
  isActive: FormControl<ITenantFeeConfig['isActive']>;
  tenant: FormControl<ITenantFeeConfig['tenant']>;
  countryPaymentMethod: FormControl<ITenantFeeConfig['countryPaymentMethod']>;
};

export type TenantFeeConfigFormGroup = FormGroup<TenantFeeConfigFormGroupContent>;

@Service()
export class TenantFeeConfigFormService {
  createTenantFeeConfigFormGroup(tenantFeeConfig?: TenantFeeConfigFormGroupInput): TenantFeeConfigFormGroup {
    const tenantFeeConfigRawValue = {
      ...this.getFormDefaults(),
      ...(tenantFeeConfig ?? { id: null }),
    };

    return new FormGroup<TenantFeeConfigFormGroupContent>({
      id: new FormControl(
        { value: tenantFeeConfigRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fixedFee: new FormControl(tenantFeeConfigRawValue.fixedFee, {
        validators: [Validators.required],
      }),
      percentageFee: new FormControl(tenantFeeConfigRawValue.percentageFee, {
        validators: [Validators.required],
      }),
      capAmount: new FormControl(tenantFeeConfigRawValue.capAmount),
      feeBearer: new FormControl(tenantFeeConfigRawValue.feeBearer, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(tenantFeeConfigRawValue.isActive, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(tenantFeeConfigRawValue.tenant, {
        validators: [Validators.required],
      }),
      countryPaymentMethod: new FormControl(tenantFeeConfigRawValue.countryPaymentMethod, {
        validators: [Validators.required],
      }),
    });
  }

  getTenantFeeConfig(form: TenantFeeConfigFormGroup): ITenantFeeConfig | NewTenantFeeConfig {
    return form.getRawValue();
  }

  resetForm(form: TenantFeeConfigFormGroup, tenantFeeConfig: TenantFeeConfigFormGroupInput): void {
    const tenantFeeConfigRawValue = { ...this.getFormDefaults(), ...tenantFeeConfig };
    form.reset({
      ...tenantFeeConfigRawValue,
      id: { value: tenantFeeConfigRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TenantFeeConfigFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
