import dayjs from 'dayjs/esm';

import { IApiKey, NewApiKey } from './api-key.model';

export const sampleWithRequiredData: IApiKey = {
  id: 1312,
  keyPrefix: 'nor whenever sometimes',
  keyHash: 'likely range',
  environment: 'LIVE',
  isActive: true,
  issuedAt: dayjs('2026-09-21T09:31'),
};

export const sampleWithPartialData: IApiKey = {
  id: 6687,
  keyPrefix: 'stealthily oof physically',
  keyHash: 'inasmuch but edible',
  environment: 'TEST',
  isActive: true,
  issuedAt: dayjs('2026-09-21T05:42'),
};

export const sampleWithFullData: IApiKey = {
  id: 28064,
  keyPrefix: 'like',
  keyHash: 'vaguely improbable',
  environment: 'TEST',
  isActive: false,
  issuedAt: dayjs('2026-09-20T20:36'),
  revokedAt: dayjs('2026-09-20T23:13'),
  graceExpiresAt: dayjs('2026-09-20T19:57'),
};

export const sampleWithNewData: NewApiKey = {
  keyPrefix: 'skateboard jaggedly glorious',
  keyHash: 'busy',
  environment: 'TEST',
  isActive: false,
  issuedAt: dayjs('2026-09-21T09:06'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
