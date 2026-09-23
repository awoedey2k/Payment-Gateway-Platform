import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';

export interface ITenantWallet {
  id: number;
  currencyCode?: string | null;
  availableBalance?: number | null;
  lockedBalance?: number | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewTenantWallet = Omit<ITenantWallet, 'id'> & { id: null };
