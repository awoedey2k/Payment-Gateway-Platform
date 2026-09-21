import dayjs from 'dayjs/esm';

import { WebhookDeliveryStatus } from 'app/entities/enumerations/webhook-delivery-status.model';
import { IWebhookSubscription } from 'app/entities/webhook-subscription/webhook-subscription.model';

export interface IWebhookDeliveryAttempt {
  id: number;
  eventType?: string | null;
  status?: keyof typeof WebhookDeliveryStatus | null;
  httpStatusCode?: number | null;
  attemptNumber?: number | null;
  attemptedAt?: dayjs.Dayjs | null;
  subscription?: Pick<IWebhookSubscription, 'id'> | null;
}

export type NewWebhookDeliveryAttempt = Omit<IWebhookDeliveryAttempt, 'id'> & { id: null };
