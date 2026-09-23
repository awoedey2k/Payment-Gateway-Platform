import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IAmlCheck, NewAmlCheck } from '../aml-check.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAmlCheck for edit and NewAmlCheckFormGroupInput for create.
 */
type AmlCheckFormGroupInput = IAmlCheck | PartialWithRequiredKeyOf<NewAmlCheck>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAmlCheck | NewAmlCheck> = Omit<T, 'checkedAt'> & {
  checkedAt?: string | null;
};

type AmlCheckFormRawValue = FormValueOf<IAmlCheck>;

type NewAmlCheckFormRawValue = FormValueOf<NewAmlCheck>;

type AmlCheckFormDefaults = Pick<NewAmlCheck, 'id' | 'checkedAt'>;

type AmlCheckFormGroupContent = {
  id: FormControl<AmlCheckFormRawValue['id'] | NewAmlCheck['id']>;
  riskScore: FormControl<AmlCheckFormRawValue['riskScore']>;
  decision: FormControl<AmlCheckFormRawValue['decision']>;
  ruleTriggered: FormControl<AmlCheckFormRawValue['ruleTriggered']>;
  checkedAt: FormControl<AmlCheckFormRawValue['checkedAt']>;
  tenant: FormControl<AmlCheckFormRawValue['tenant']>;
  transaction: FormControl<AmlCheckFormRawValue['transaction']>;
};

export type AmlCheckFormGroup = FormGroup<AmlCheckFormGroupContent>;

@Service()
export class AmlCheckFormService {
  createAmlCheckFormGroup(amlCheck?: AmlCheckFormGroupInput): AmlCheckFormGroup {
    const amlCheckRawValue = this.convertAmlCheckToAmlCheckRawValue({
      ...this.getFormDefaults(),
      ...(amlCheck ?? { id: null }),
    });

    return new FormGroup<AmlCheckFormGroupContent>({
      id: new FormControl(
        { value: amlCheckRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      riskScore: new FormControl(amlCheckRawValue.riskScore, {
        validators: [Validators.required],
      }),
      decision: new FormControl(amlCheckRawValue.decision, {
        validators: [Validators.required],
      }),
      ruleTriggered: new FormControl(amlCheckRawValue.ruleTriggered),
      checkedAt: new FormControl(amlCheckRawValue.checkedAt, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(amlCheckRawValue.tenant),
      transaction: new FormControl(amlCheckRawValue.transaction),
    });
  }

  getAmlCheck(form: AmlCheckFormGroup): IAmlCheck | NewAmlCheck {
    return this.convertAmlCheckRawValueToAmlCheck(form.getRawValue());
  }

  resetForm(form: AmlCheckFormGroup, amlCheck: AmlCheckFormGroupInput): void {
    const amlCheckRawValue = this.convertAmlCheckToAmlCheckRawValue({ ...this.getFormDefaults(), ...amlCheck });
    form.reset({
      ...amlCheckRawValue,
      id: { value: amlCheckRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AmlCheckFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      checkedAt: currentTime,
    };
  }

  private convertAmlCheckRawValueToAmlCheck(rawAmlCheck: AmlCheckFormRawValue | NewAmlCheckFormRawValue): IAmlCheck | NewAmlCheck {
    return {
      ...rawAmlCheck,
      checkedAt: dayjs(rawAmlCheck.checkedAt, DATE_TIME_FORMAT),
    };
  }

  private convertAmlCheckToAmlCheckRawValue(
    amlCheck: IAmlCheck | (Partial<NewAmlCheck> & AmlCheckFormDefaults),
  ): AmlCheckFormRawValue | PartialWithRequiredKeyOf<NewAmlCheckFormRawValue> {
    return {
      ...amlCheck,
      checkedAt: amlCheck.checkedAt ? amlCheck.checkedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
