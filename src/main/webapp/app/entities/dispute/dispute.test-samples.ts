import dayjs from 'dayjs/esm';

import { IDispute, NewDispute } from './dispute.model';

export const sampleWithRequiredData: IDispute = {
  id: 23664,
  caseReference: 'amused midst',
  amount: 17271.27,
  currencyCode: 'CLP',
  reasonCode: 'bashfully taro alb',
  status: 'CLOSED',
  dueDate: dayjs('2026-09-21T02:26'),
};

export const sampleWithPartialData: IDispute = {
  id: 29070,
  caseReference: 'impolite that',
  amount: 21213.95,
  currencyCode: 'EGP',
  reasonCode: 'per seriously cheerful',
  status: 'CLOSED',
  dueDate: dayjs('2026-09-20T21:21'),
};

export const sampleWithFullData: IDispute = {
  id: 10117,
  caseReference: 'given lovingly ignorance',
  amount: 8585.35,
  currencyCode: 'CDF',
  reasonCode: 'think orange',
  reasonDescription: 'cornet',
  status: 'EVIDENCE_SUBMITTED',
  dueDate: dayjs('2026-09-21T06:05'),
  evidenceSubmittedAt: dayjs('2026-09-21T17:21'),
  resolvedAt: dayjs('2026-09-20T22:31'),
};

export const sampleWithNewData: NewDispute = {
  caseReference: 'psst anenst',
  amount: 28182.13,
  currencyCode: 'GHS',
  reasonCode: 'sad',
  status: 'UNDER_REVIEW',
  dueDate: dayjs('2026-09-20T21:36'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
