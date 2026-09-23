import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';

export interface ITransaction {
  id: number;
  reference?: string | null;
  tenantReference?: string | null;
  status?: keyof typeof TransactionStatus | null;
  amount?: number | null;
  feeAmount?: number | null;
  netAmount?: number | null;
  currencyCode?: string | null;
  countryCode?: string | null;
  paymentMethodCode?: string | null;
  idempotencyKey?: string | null;
  customerEmail?: string | null;
  customerPhone?: string | null;
  createdAt?: dayjs.Dayjs | null;
  completedAt?: dayjs.Dayjs | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewTransaction = Omit<ITransaction, 'id'> & { id: null };
