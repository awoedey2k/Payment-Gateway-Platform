import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRoutingRule, NewRoutingRule } from '../routing-rule.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRoutingRule for edit and NewRoutingRuleFormGroupInput for create.
 */
type RoutingRuleFormGroupInput = IRoutingRule | PartialWithRequiredKeyOf<NewRoutingRule>;

type RoutingRuleFormDefaults = Pick<NewRoutingRule, 'id' | 'isActive'>;

type RoutingRuleFormGroupContent = {
  id: FormControl<IRoutingRule['id'] | NewRoutingRule['id']>;
  priority: FormControl<IRoutingRule['priority']>;
  scope: FormControl<IRoutingRule['scope']>;
  countryCode: FormControl<IRoutingRule['countryCode']>;
  currencyCode: FormControl<IRoutingRule['currencyCode']>;
  cardBrand: FormControl<IRoutingRule['cardBrand']>;
  primaryAdapter: FormControl<IRoutingRule['primaryAdapter']>;
  fallbackAdapter: FormControl<IRoutingRule['fallbackAdapter']>;
  maxRetries: FormControl<IRoutingRule['maxRetries']>;
  isActive: FormControl<IRoutingRule['isActive']>;
  tenant: FormControl<IRoutingRule['tenant']>;
};

export type RoutingRuleFormGroup = FormGroup<RoutingRuleFormGroupContent>;

@Service()
export class RoutingRuleFormService {
  createRoutingRuleFormGroup(routingRule?: RoutingRuleFormGroupInput): RoutingRuleFormGroup {
    const routingRuleRawValue = {
      ...this.getFormDefaults(),
      ...(routingRule ?? { id: null }),
    };

    return new FormGroup<RoutingRuleFormGroupContent>({
      id: new FormControl(
        { value: routingRuleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      priority: new FormControl(routingRuleRawValue.priority, {
        validators: [Validators.required],
      }),
      scope: new FormControl(routingRuleRawValue.scope, {
        validators: [Validators.required],
      }),
      countryCode: new FormControl(routingRuleRawValue.countryCode),
      currencyCode: new FormControl(routingRuleRawValue.currencyCode),
      cardBrand: new FormControl(routingRuleRawValue.cardBrand),
      primaryAdapter: new FormControl(routingRuleRawValue.primaryAdapter, {
        validators: [Validators.required],
      }),
      fallbackAdapter: new FormControl(routingRuleRawValue.fallbackAdapter),
      maxRetries: new FormControl(routingRuleRawValue.maxRetries, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(routingRuleRawValue.isActive, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(routingRuleRawValue.tenant),
    });
  }

  getRoutingRule(form: RoutingRuleFormGroup): IRoutingRule | NewRoutingRule {
    return form.getRawValue();
  }

  resetForm(form: RoutingRuleFormGroup, routingRule: RoutingRuleFormGroupInput): void {
    const routingRuleRawValue = { ...this.getFormDefaults(), ...routingRule };
    form.reset({
      ...routingRuleRawValue,
      id: { value: routingRuleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RoutingRuleFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
