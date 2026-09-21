import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { DisputeStatus } from 'app/entities/enumerations/dispute-status.model';
import { ITransaction } from 'app/entities/transaction/transaction.model';

export interface IDispute {
  id: number;
  caseReference?: string | null;
  amount?: number | null;
  currencyCode?: string | null;
  reasonCode?: string | null;
  reasonDescription?: string | null;
  status?: keyof typeof DisputeStatus | null;
  dueDate?: dayjs.Dayjs | null;
  evidenceSubmittedAt?: dayjs.Dayjs | null;
  resolvedAt?: dayjs.Dayjs | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
  transaction?: Pick<ITransaction, 'id'> | null;
}

export type NewDispute = Omit<IDispute, 'id'> & { id: null };
