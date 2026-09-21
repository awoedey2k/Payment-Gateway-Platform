import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';

export interface IWebhookSubscription {
  id: number;
  targetUrl?: string | null;
  secretHash?: string | null;
  isActive?: boolean | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewWebhookSubscription = Omit<IWebhookSubscription, 'id'> & { id: null };
