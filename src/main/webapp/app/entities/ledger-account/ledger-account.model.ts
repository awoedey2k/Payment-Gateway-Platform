import { LedgerAccountType } from 'app/entities/enumerations/ledger-account-type.model';

export interface ILedgerAccount {
  id: number;
  accountCode?: string | null;
  accountType?: keyof typeof LedgerAccountType | null;
  currencyCode?: string | null;
}

export type NewLedgerAccount = Omit<ILedgerAccount, 'id'> & { id: null };
