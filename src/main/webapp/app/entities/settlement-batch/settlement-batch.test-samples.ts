import dayjs from 'dayjs/esm';

import { ISettlementBatch, NewSettlementBatch } from './settlement-batch.model';

export const sampleWithRequiredData: ISettlementBatch = {
  id: 17944,
  reference: 'untidy',
  status: 'PROCESSING',
  totalAmount: 26765.15,
  currencyCode: 'GNF',
  scheduledAt: dayjs('2026-09-21T11:58'),
};

export const sampleWithPartialData: ISettlementBatch = {
  id: 17095,
  reference: 'accountability allegation',
  status: 'FAILED',
  totalAmount: 21297.36,
  currencyCode: 'COP',
  scheduledAt: dayjs('2026-09-20T23:44'),
};

export const sampleWithFullData: ISettlementBatch = {
  id: 2175,
  reference: 'pop funny',
  status: 'FAILED',
  totalAmount: 25922.7,
  currencyCode: 'UAH',
  scheduledAt: dayjs('2026-09-20T19:53'),
  completedAt: dayjs('2026-09-21T07:07'),
};

export const sampleWithNewData: NewSettlementBatch = {
  reference: 'machine hold',
  status: 'SCHEDULED',
  totalAmount: 24279.32,
  currencyCode: 'EUR',
  scheduledAt: dayjs('2026-09-20T23:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
