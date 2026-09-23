import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { AmlDecision } from 'app/entities/enumerations/aml-decision.model';
import { ITransaction } from 'app/entities/transaction/transaction.model';

export interface IAmlCheck {
  id: number;
  riskScore?: number | null;
  decision?: keyof typeof AmlDecision | null;
  ruleTriggered?: string | null;
  checkedAt?: dayjs.Dayjs | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
  transaction?: Pick<ITransaction, 'id'> | null;
}

export type NewAmlCheck = Omit<IAmlCheck, 'id'> & { id: null };
