import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITenantDomain, NewTenantDomain } from '../tenant-domain.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenantDomain for edit and NewTenantDomainFormGroupInput for create.
 */
type TenantDomainFormGroupInput = ITenantDomain | PartialWithRequiredKeyOf<NewTenantDomain>;

type TenantDomainFormDefaults = Pick<NewTenantDomain, 'id' | 'isVerified'>;

type TenantDomainFormGroupContent = {
  id: FormControl<ITenantDomain['id'] | NewTenantDomain['id']>;
  customDomain: FormControl<ITenantDomain['customDomain']>;
  supportedLocales: FormControl<ITenantDomain['supportedLocales']>;
  defaultLocale: FormControl<ITenantDomain['defaultLocale']>;
  fallbackLocale: FormControl<ITenantDomain['fallbackLocale']>;
  isVerified: FormControl<ITenantDomain['isVerified']>;
  tenant: FormControl<ITenantDomain['tenant']>;
};

export type TenantDomainFormGroup = FormGroup<TenantDomainFormGroupContent>;

@Service()
export class TenantDomainFormService {
  createTenantDomainFormGroup(tenantDomain?: TenantDomainFormGroupInput): TenantDomainFormGroup {
    const tenantDomainRawValue = {
      ...this.getFormDefaults(),
      ...(tenantDomain ?? { id: null }),
    };

    return new FormGroup<TenantDomainFormGroupContent>({
      id: new FormControl(
        { value: tenantDomainRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      customDomain: new FormControl(tenantDomainRawValue.customDomain, {
        validators: [Validators.required],
      }),
      supportedLocales: new FormControl(tenantDomainRawValue.supportedLocales),
      defaultLocale: new FormControl(tenantDomainRawValue.defaultLocale),
      fallbackLocale: new FormControl(tenantDomainRawValue.fallbackLocale),
      isVerified: new FormControl(tenantDomainRawValue.isVerified, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(tenantDomainRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getTenantDomain(form: TenantDomainFormGroup): ITenantDomain | NewTenantDomain {
    return form.getRawValue();
  }

  resetForm(form: TenantDomainFormGroup, tenantDomain: TenantDomainFormGroupInput): void {
    const tenantDomainRawValue = { ...this.getFormDefaults(), ...tenantDomain };
    form.reset({
      ...tenantDomainRawValue,
      id: { value: tenantDomainRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TenantDomainFormDefaults {
    return {
      id: null,
      isVerified: false,
    };
  }
}
