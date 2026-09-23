import dayjs from 'dayjs/esm';

export interface IForexRate {
  id: number;
  baseCurrency?: string | null;
  quoteCurrency?: string | null;
  rate?: number | null;
  platformSpreadBps?: number | null;
  lockedAt?: dayjs.Dayjs | null;
  expiresAt?: dayjs.Dayjs | null;
}

export type NewForexRate = Omit<IForexRate, 'id'> & { id: null };
