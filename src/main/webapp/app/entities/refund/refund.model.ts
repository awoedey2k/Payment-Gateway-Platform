import dayjs from 'dayjs/esm';

import { RefundReason } from 'app/entities/enumerations/refund-reason.model';
import { RefundStatus } from 'app/entities/enumerations/refund-status.model';
import { ITransaction } from 'app/entities/transaction/transaction.model';

export interface IRefund {
  id: number;
  reference?: string | null;
  amount?: number | null;
  reason?: keyof typeof RefundReason | null;
  status?: keyof typeof RefundStatus | null;
  createdAt?: dayjs.Dayjs | null;
  transaction?: Pick<ITransaction, 'id'> | null;
}

export type NewRefund = Omit<IRefund, 'id'> & { id: null };
