import { ITenantDomain, NewTenantDomain } from './tenant-domain.model';

export const sampleWithRequiredData: ITenantDomain = {
  id: 6802,
  customDomain: 'wound gah',
  isVerified: false,
};

export const sampleWithPartialData: ITenantDomain = {
  id: 27711,
  customDomain: 'past mysterious',
  supportedLocales: 'derby equatorial',
  isVerified: false,
};

export const sampleWithFullData: ITenantDomain = {
  id: 6582,
  customDomain: 'limping sharply',
  supportedLocales: 'whistle apropos',
  isVerified: true,
};

export const sampleWithNewData: NewTenantDomain = {
  customDomain: 'furthermore overstay',
  isVerified: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
