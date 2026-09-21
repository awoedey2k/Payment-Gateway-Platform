import dayjs from 'dayjs/esm';

export interface IJournalEntry {
  id: number;
  reference?: string | null;
  description?: string | null;
  postedAt?: dayjs.Dayjs | null;
}

export type NewJournalEntry = Omit<IJournalEntry, 'id'> & { id: null };
