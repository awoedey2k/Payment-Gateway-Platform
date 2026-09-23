import { IWebhookSubscription, NewWebhookSubscription } from './webhook-subscription.model';

export const sampleWithRequiredData: IWebhookSubscription = {
  id: 11235,
  targetUrl: 'deploy up',
  secretHash: 'ha',
  isActive: false,
};

export const sampleWithPartialData: IWebhookSubscription = {
  id: 4657,
  targetUrl: 'enormously',
  secretHash: 'pale',
  isActive: false,
};

export const sampleWithFullData: IWebhookSubscription = {
  id: 31609,
  targetUrl: 'indeed',
  secretHash: 'angelic',
  isActive: true,
};

export const sampleWithNewData: NewWebhookSubscription = {
  targetUrl: 'psst before',
  secretHash: 'expensive with',
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
