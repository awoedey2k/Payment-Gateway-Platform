import dayjs from 'dayjs/esm';

import { ITenantDirector, NewTenantDirector } from './tenant-director.model';

export const sampleWithRequiredData: ITenantDirector = {
  id: 9755,
  fullName: 'unethically suspiciously',
  dateOfBirth: dayjs('2026-09-21'),
  nationality: 'ZA',
  identificationType: 'DRIVERS_LICENSE',
  identificationNumber: 'bah',
};

export const sampleWithPartialData: ITenantDirector = {
  id: 4108,
  fullName: 'anxiously',
  dateOfBirth: dayjs('2026-09-21'),
  nationality: 'NG',
  identificationType: 'NATIONAL_ID',
  identificationNumber: 'vaguely uh-huh damp',
};

export const sampleWithFullData: ITenantDirector = {
  id: 2578,
  fullName: 'ad',
  dateOfBirth: dayjs('2026-09-21'),
  nationality: 'ZA',
  identificationType: 'NATIONAL_ID',
  identificationNumber: 'screw before boohoo',
};

export const sampleWithNewData: NewTenantDirector = {
  fullName: 'since helpfully always',
  dateOfBirth: dayjs('2026-09-21'),
  nationality: 'GH',
  identificationType: 'NATIONAL_ID',
  identificationNumber: 'equally jubilant community',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
