import dayjs from 'dayjs/esm';

import { IWebhookDeliveryAttempt, NewWebhookDeliveryAttempt } from './webhook-delivery-attempt.model';

export const sampleWithRequiredData: IWebhookDeliveryAttempt = {
  id: 27604,
  eventType: 'despite',
  status: 'PENDING',
  attemptNumber: 19391,
  attemptedAt: dayjs('2026-09-21T14:00'),
};

export const sampleWithPartialData: IWebhookDeliveryAttempt = {
  id: 27508,
  eventType: 'priesthood',
  status: 'FAILED',
  httpStatusCode: 31718,
  attemptNumber: 19478,
  attemptedAt: dayjs('2026-09-20T22:38'),
};

export const sampleWithFullData: IWebhookDeliveryAttempt = {
  id: 15853,
  eventType: 'petticoat extricate',
  status: 'DELIVERED',
  httpStatusCode: 12831,
  attemptNumber: 7036,
  attemptedAt: dayjs('2026-09-20T23:27'),
};

export const sampleWithNewData: NewWebhookDeliveryAttempt = {
  eventType: 'whoever larva',
  status: 'DELIVERED',
  attemptNumber: 22258,
  attemptedAt: dayjs('2026-09-21T01:03'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
