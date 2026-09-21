import dayjs from 'dayjs/esm';

import { IAmlCheck, NewAmlCheck } from './aml-check.model';

export const sampleWithRequiredData: IAmlCheck = {
  id: 23868,
  riskScore: 9017,
  decision: 'TIER_REVIEW',
  checkedAt: dayjs('2026-09-21T16:53'),
};

export const sampleWithPartialData: IAmlCheck = {
  id: 26098,
  riskScore: 15841,
  decision: 'ALLOW',
  ruleTriggered: 'even what',
  checkedAt: dayjs('2026-09-21T08:32'),
};

export const sampleWithFullData: IAmlCheck = {
  id: 7859,
  riskScore: 22803,
  decision: 'ALLOW',
  ruleTriggered: 'versus when unabashedly',
  checkedAt: dayjs('2026-09-21T00:02'),
};

export const sampleWithNewData: NewAmlCheck = {
  riskScore: 9482,
  decision: 'CHALLENGE',
  checkedAt: dayjs('2026-09-20T18:17'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
