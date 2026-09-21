import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { SettlementBatchStatus } from 'app/entities/enumerations/settlement-batch-status.model';

export interface ISettlementBatch {
  id: number;
  reference?: string | null;
  status?: keyof typeof SettlementBatchStatus | null;
  totalAmount?: number | null;
  currencyCode?: string | null;
  scheduledAt?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewSettlementBatch = Omit<ISettlementBatch, 'id'> & { id: null };
