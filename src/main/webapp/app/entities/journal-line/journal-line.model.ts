import { IJournalEntry } from 'app/entities/journal-entry/journal-entry.model';
import { ILedgerAccount } from 'app/entities/ledger-account/ledger-account.model';

export interface IJournalLine {
  id: number;
  debitAmount?: number | null;
  creditAmount?: number | null;
  account?: Pick<ILedgerAccount, 'id'> | null;
  journalEntry?: Pick<IJournalEntry, 'id'> | null;
}

export type NewJournalLine = Omit<IJournalLine, 'id'> & { id: null };
