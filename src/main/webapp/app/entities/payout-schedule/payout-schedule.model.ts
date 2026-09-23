import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { PayoutFrequency } from 'app/entities/enumerations/payout-frequency.model';

export interface IPayoutSchedule {
  id: number;
  frequencyMode?: keyof typeof PayoutFrequency | null;
  thresholdAmount?: number | null;
  isActive?: boolean | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewPayoutSchedule = Omit<IPayoutSchedule, 'id'> & { id: null };
