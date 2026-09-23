import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IWebhookDeliveryAttempt, NewWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWebhookDeliveryAttempt for edit and NewWebhookDeliveryAttemptFormGroupInput for create.
 */
type WebhookDeliveryAttemptFormGroupInput = IWebhookDeliveryAttempt | PartialWithRequiredKeyOf<NewWebhookDeliveryAttempt>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWebhookDeliveryAttempt | NewWebhookDeliveryAttempt> = Omit<T, 'attemptedAt'> & {
  attemptedAt?: string | null;
};

type WebhookDeliveryAttemptFormRawValue = FormValueOf<IWebhookDeliveryAttempt>;

type NewWebhookDeliveryAttemptFormRawValue = FormValueOf<NewWebhookDeliveryAttempt>;

type WebhookDeliveryAttemptFormDefaults = Pick<NewWebhookDeliveryAttempt, 'id' | 'attemptedAt'>;

type WebhookDeliveryAttemptFormGroupContent = {
  id: FormControl<WebhookDeliveryAttemptFormRawValue['id'] | NewWebhookDeliveryAttempt['id']>;
  eventType: FormControl<WebhookDeliveryAttemptFormRawValue['eventType']>;
  status: FormControl<WebhookDeliveryAttemptFormRawValue['status']>;
  httpStatusCode: FormControl<WebhookDeliveryAttemptFormRawValue['httpStatusCode']>;
  attemptNumber: FormControl<WebhookDeliveryAttemptFormRawValue['attemptNumber']>;
  attemptedAt: FormControl<WebhookDeliveryAttemptFormRawValue['attemptedAt']>;
  subscription: FormControl<WebhookDeliveryAttemptFormRawValue['subscription']>;
};

export type WebhookDeliveryAttemptFormGroup = FormGroup<WebhookDeliveryAttemptFormGroupContent>;

@Service()
export class WebhookDeliveryAttemptFormService {
  createWebhookDeliveryAttemptFormGroup(webhookDeliveryAttempt?: WebhookDeliveryAttemptFormGroupInput): WebhookDeliveryAttemptFormGroup {
    const webhookDeliveryAttemptRawValue = this.convertWebhookDeliveryAttemptToWebhookDeliveryAttemptRawValue({
      ...this.getFormDefaults(),
      ...(webhookDeliveryAttempt ?? { id: null }),
    });

    return new FormGroup<WebhookDeliveryAttemptFormGroupContent>({
      id: new FormControl(
        { value: webhookDeliveryAttemptRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      eventType: new FormControl(webhookDeliveryAttemptRawValue.eventType, {
        validators: [Validators.required],
      }),
      status: new FormControl(webhookDeliveryAttemptRawValue.status, {
        validators: [Validators.required],
      }),
      httpStatusCode: new FormControl(webhookDeliveryAttemptRawValue.httpStatusCode),
      attemptNumber: new FormControl(webhookDeliveryAttemptRawValue.attemptNumber, {
        validators: [Validators.required],
      }),
      attemptedAt: new FormControl(webhookDeliveryAttemptRawValue.attemptedAt, {
        validators: [Validators.required],
      }),
      subscription: new FormControl(webhookDeliveryAttemptRawValue.subscription, {
        validators: [Validators.required],
      }),
    });
  }

  getWebhookDeliveryAttempt(form: WebhookDeliveryAttemptFormGroup): IWebhookDeliveryAttempt | NewWebhookDeliveryAttempt {
    return this.convertWebhookDeliveryAttemptRawValueToWebhookDeliveryAttempt(form.getRawValue());
  }

  resetForm(form: WebhookDeliveryAttemptFormGroup, webhookDeliveryAttempt: WebhookDeliveryAttemptFormGroupInput): void {
    const webhookDeliveryAttemptRawValue = this.convertWebhookDeliveryAttemptToWebhookDeliveryAttemptRawValue({
      ...this.getFormDefaults(),
      ...webhookDeliveryAttempt,
    });
    form.reset({
      ...webhookDeliveryAttemptRawValue,
      id: { value: webhookDeliveryAttemptRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): WebhookDeliveryAttemptFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      attemptedAt: currentTime,
    };
  }

  private convertWebhookDeliveryAttemptRawValueToWebhookDeliveryAttempt(
    rawWebhookDeliveryAttempt: WebhookDeliveryAttemptFormRawValue | NewWebhookDeliveryAttemptFormRawValue,
  ): IWebhookDeliveryAttempt | NewWebhookDeliveryAttempt {
    return {
      ...rawWebhookDeliveryAttempt,
      attemptedAt: dayjs(rawWebhookDeliveryAttempt.attemptedAt, DATE_TIME_FORMAT),
    };
  }

  private convertWebhookDeliveryAttemptToWebhookDeliveryAttemptRawValue(
    webhookDeliveryAttempt: IWebhookDeliveryAttempt | (Partial<NewWebhookDeliveryAttempt> & WebhookDeliveryAttemptFormDefaults),
  ): WebhookDeliveryAttemptFormRawValue | PartialWithRequiredKeyOf<NewWebhookDeliveryAttemptFormRawValue> {
    return {
      ...webhookDeliveryAttempt,
      attemptedAt: webhookDeliveryAttempt.attemptedAt ? webhookDeliveryAttempt.attemptedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
