import dayjs from 'dayjs/esm';

import { ITransaction, NewTransaction } from './transaction.model';

export const sampleWithRequiredData: ITransaction = {
  id: 3793,
  reference: 'dearly but aha',
  status: 'FAILED',
  amount: 18876.33,
  feeAmount: 20652.5,
  netAmount: 30553.27,
  currencyCode: 'USD',
  countryCode: 'SV',
  paymentMethodCode: 'as triangular outset',
  idempotencyKey: 'hm pension softly',
  createdAt: dayjs('2026-09-21T06:50'),
};

export const sampleWithPartialData: ITransaction = {
  id: 11058,
  reference: 'somber sadly fine',
  status: 'SUCCESSFUL',
  amount: 9317.8,
  feeAmount: 6055.61,
  netAmount: 30881.19,
  currencyCode: 'SLE',
  countryCode: 'TR',
  paymentMethodCode: 'utter or',
  idempotencyKey: 'across uh-huh ew',
  customerEmail: 'unaccountably for',
  createdAt: dayjs('2026-09-21T15:24'),
  completedAt: dayjs('2026-09-21T12:18'),
};

export const sampleWithFullData: ITransaction = {
  id: 20285,
  reference: 'cloudy',
  tenantReference: 'hm into boo',
  status: 'FAILED',
  amount: 22793.06,
  feeAmount: 6372.26,
  netAmount: 14309.74,
  currencyCode: 'HKD',
  countryCode: 'SM',
  paymentMethodCode: 'evenly',
  idempotencyKey: 'via',
  customerEmail: 'swing instead',
  customerPhone: 'king',
  createdAt: dayjs('2026-09-21T01:13'),
  completedAt: dayjs('2026-09-21T01:04'),
};

export const sampleWithNewData: NewTransaction = {
  reference: 'custom minty beneficial',
  status: 'PENDING',
  amount: 23150.88,
  feeAmount: 30200.69,
  netAmount: 16372.17,
  currencyCode: 'GYD',
  countryCode: 'TR',
  paymentMethodCode: 'ouch impeccable healthily',
  idempotencyKey: 'acidly wisecrack oof',
  createdAt: dayjs('2026-09-20T21:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
