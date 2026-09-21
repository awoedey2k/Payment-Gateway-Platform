import dayjs from 'dayjs/esm';

import { IForexRate, NewForexRate } from './forex-rate.model';

export const sampleWithRequiredData: IForexRate = {
  id: 22264,
  baseCurrency: 'ah boohoo huddle',
  quoteCurrency: 'season',
  rate: 26131.79,
  platformSpreadBps: 18966,
  lockedAt: dayjs('2026-09-21T09:46'),
  expiresAt: dayjs('2026-09-20T21:20'),
};

export const sampleWithPartialData: IForexRate = {
  id: 14050,
  baseCurrency: 'as until disadvantage',
  quoteCurrency: 'filter provided celebrate',
  rate: 17934.34,
  platformSpreadBps: 905,
  lockedAt: dayjs('2026-09-21T16:59'),
  expiresAt: dayjs('2026-09-20T19:51'),
};

export const sampleWithFullData: IForexRate = {
  id: 7218,
  baseCurrency: 'before imaginary',
  quoteCurrency: 'dearest even',
  rate: 20317.25,
  platformSpreadBps: 20999,
  lockedAt: dayjs('2026-09-21T09:21'),
  expiresAt: dayjs('2026-09-20T21:33'),
};

export const sampleWithNewData: NewForexRate = {
  baseCurrency: 'whether hm',
  quoteCurrency: 'tenderly fussy mixture',
  rate: 1882.03,
  platformSpreadBps: 2893,
  lockedAt: dayjs('2026-09-20T20:19'),
  expiresAt: dayjs('2026-09-21T14:18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
