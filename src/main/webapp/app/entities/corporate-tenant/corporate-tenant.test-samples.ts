import dayjs from 'dayjs/esm';

import { ICorporateTenant, NewCorporateTenant } from './corporate-tenant.model';

export const sampleWithRequiredData: ICorporateTenant = {
  id: 22917,
  legalBusinessName: 'lest',
  businessRegistrationNumber: 'overcooked inasmuch',
  taxIdentificationNumber: 'uh-huh yet',
  operatingJurisdiction: 'GH',
  status: 'SUSPENDED',
  kycStatus: 'APPROVED',
  createdAt: dayjs('2026-09-21T05:44'),
};

export const sampleWithPartialData: ICorporateTenant = {
  id: 11175,
  legalBusinessName: 'but besides considering',
  businessRegistrationNumber: 'yum reborn boohoo',
  taxIdentificationNumber: 'boohoo trial times',
  operatingJurisdiction: 'US',
  status: 'PENDING_REVIEW',
  kycStatus: 'NOT_STARTED',
  createdAt: dayjs('2026-09-21T08:05'),
};

export const sampleWithFullData: ICorporateTenant = {
  id: 15540,
  legalBusinessName: 'that gray',
  businessRegistrationNumber: 'fervently design huzzah',
  taxIdentificationNumber: 'er while whether',
  operatingJurisdiction: 'DE',
  status: 'SUSPENDED',
  kycStatus: 'REJECTED',
  riskScore: 30687,
  createdAt: dayjs('2026-09-21T08:03'),
  activatedAt: dayjs('2026-09-20T21:51'),
};

export const sampleWithNewData: NewCorporateTenant = {
  legalBusinessName: 'once excepting',
  businessRegistrationNumber: 'reproachfully blight phew',
  taxIdentificationNumber: 'questioningly',
  operatingJurisdiction: 'DE',
  status: 'PENDING_REVIEW',
  kycStatus: 'REJECTED',
  createdAt: dayjs('2026-09-21T13:08'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
