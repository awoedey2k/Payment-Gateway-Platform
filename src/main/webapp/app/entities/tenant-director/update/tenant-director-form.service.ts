import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITenantDirector, NewTenantDirector } from '../tenant-director.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITenantDirector for edit and NewTenantDirectorFormGroupInput for create.
 */
type TenantDirectorFormGroupInput = ITenantDirector | PartialWithRequiredKeyOf<NewTenantDirector>;

type TenantDirectorFormDefaults = Pick<NewTenantDirector, 'id'>;

type TenantDirectorFormGroupContent = {
  id: FormControl<ITenantDirector['id'] | NewTenantDirector['id']>;
  fullName: FormControl<ITenantDirector['fullName']>;
  dateOfBirth: FormControl<ITenantDirector['dateOfBirth']>;
  nationality: FormControl<ITenantDirector['nationality']>;
  identificationType: FormControl<ITenantDirector['identificationType']>;
  identificationNumber: FormControl<ITenantDirector['identificationNumber']>;
  tenant: FormControl<ITenantDirector['tenant']>;
};

export type TenantDirectorFormGroup = FormGroup<TenantDirectorFormGroupContent>;

@Service()
export class TenantDirectorFormService {
  createTenantDirectorFormGroup(tenantDirector?: TenantDirectorFormGroupInput): TenantDirectorFormGroup {
    const tenantDirectorRawValue = {
      ...this.getFormDefaults(),
      ...(tenantDirector ?? { id: null }),
    };

    return new FormGroup<TenantDirectorFormGroupContent>({
      id: new FormControl(
        { value: tenantDirectorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fullName: new FormControl(tenantDirectorRawValue.fullName, {
        validators: [Validators.required],
      }),
      dateOfBirth: new FormControl(tenantDirectorRawValue.dateOfBirth, {
        validators: [Validators.required],
      }),
      nationality: new FormControl(tenantDirectorRawValue.nationality, {
        validators: [Validators.required],
      }),
      identificationType: new FormControl(tenantDirectorRawValue.identificationType, {
        validators: [Validators.required],
      }),
      identificationNumber: new FormControl(tenantDirectorRawValue.identificationNumber, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(tenantDirectorRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getTenantDirector(form: TenantDirectorFormGroup): ITenantDirector | NewTenantDirector {
    return form.getRawValue();
  }

  resetForm(form: TenantDirectorFormGroup, tenantDirector: TenantDirectorFormGroupInput): void {
    const tenantDirectorRawValue = { ...this.getFormDefaults(), ...tenantDirector };
    form.reset({
      ...tenantDirectorRawValue,
      id: { value: tenantDirectorRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TenantDirectorFormDefaults {
    return {
      id: null,
    };
  }
}
