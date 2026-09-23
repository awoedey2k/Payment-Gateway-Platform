import dayjs from 'dayjs/esm';

import { IRefund, NewRefund } from './refund.model';

export const sampleWithRequiredData: IRefund = {
  id: 32388,
  reference: 'closely gosh',
  amount: 7456.95,
  reason: 'OTHER',
  status: 'FAILED',
  createdAt: dayjs('2026-09-21T02:36'),
};

export const sampleWithPartialData: IRefund = {
  id: 20782,
  reference: 'yum',
  amount: 16336.37,
  reason: 'FRAUD',
  status: 'PENDING',
  createdAt: dayjs('2026-09-21T08:46'),
};

export const sampleWithFullData: IRefund = {
  id: 11754,
  reference: 'twine',
  amount: 4219.19,
  reason: 'CUSTOMER_REQUEST_RETURN',
  status: 'PENDING',
  createdAt: dayjs('2026-09-21T06:43'),
};

export const sampleWithNewData: NewRefund = {
  reference: 'unexpectedly department usually',
  amount: 9193.59,
  reason: 'FRAUD',
  status: 'FAILED',
  createdAt: dayjs('2026-09-20T23:32'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
