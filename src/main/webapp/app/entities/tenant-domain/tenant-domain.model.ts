import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';

export interface ITenantDomain {
  id: number;
  customDomain?: string | null;
  supportedLocales?: string | null;
  isVerified?: boolean | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewTenantDomain = Omit<ITenantDomain, 'id'> & { id: null };
