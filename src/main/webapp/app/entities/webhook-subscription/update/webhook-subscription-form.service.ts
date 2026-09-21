import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IWebhookSubscription, NewWebhookSubscription } from '../webhook-subscription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWebhookSubscription for edit and NewWebhookSubscriptionFormGroupInput for create.
 */
type WebhookSubscriptionFormGroupInput = IWebhookSubscription | PartialWithRequiredKeyOf<NewWebhookSubscription>;

type WebhookSubscriptionFormDefaults = Pick<NewWebhookSubscription, 'id' | 'isActive'>;

type WebhookSubscriptionFormGroupContent = {
  id: FormControl<IWebhookSubscription['id'] | NewWebhookSubscription['id']>;
  targetUrl: FormControl<IWebhookSubscription['targetUrl']>;
  secretHash: FormControl<IWebhookSubscription['secretHash']>;
  isActive: FormControl<IWebhookSubscription['isActive']>;
  tenant: FormControl<IWebhookSubscription['tenant']>;
};

export type WebhookSubscriptionFormGroup = FormGroup<WebhookSubscriptionFormGroupContent>;

@Service()
export class WebhookSubscriptionFormService {
  createWebhookSubscriptionFormGroup(webhookSubscription?: WebhookSubscriptionFormGroupInput): WebhookSubscriptionFormGroup {
    const webhookSubscriptionRawValue = {
      ...this.getFormDefaults(),
      ...(webhookSubscription ?? { id: null }),
    };

    return new FormGroup<WebhookSubscriptionFormGroupContent>({
      id: new FormControl(
        { value: webhookSubscriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      targetUrl: new FormControl(webhookSubscriptionRawValue.targetUrl, {
        validators: [Validators.required],
      }),
      secretHash: new FormControl(webhookSubscriptionRawValue.secretHash, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(webhookSubscriptionRawValue.isActive, {
        validators: [Validators.required],
      }),
      tenant: new FormControl(webhookSubscriptionRawValue.tenant, {
        validators: [Validators.required],
      }),
    });
  }

  getWebhookSubscription(form: WebhookSubscriptionFormGroup): IWebhookSubscription | NewWebhookSubscription {
    return form.getRawValue();
  }

  resetForm(form: WebhookSubscriptionFormGroup, webhookSubscription: WebhookSubscriptionFormGroupInput): void {
    const webhookSubscriptionRawValue = { ...this.getFormDefaults(), ...webhookSubscription };
    form.reset({
      ...webhookSubscriptionRawValue,
      id: { value: webhookSubscriptionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): WebhookSubscriptionFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
