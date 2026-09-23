import { ITenantDomain, NewTenantDomain } from './tenant-domain.model';

export const sampleWithRequiredData: ITenantDomain = {
  id: 6802,
  customDomain: 'wound gah',
  isVerified: false,
};

export const sampleWithPartialData: ITenantDomain = {
  id: 14969,
  customDomain: 'wearily',
  supportedLocales: 'hollow cop-out',
  isVerified: true,
};

export const sampleWithFullData: ITenantDomain = {
  id: 6582,
  customDomain: 'limping sharply',
  supportedLocales: 'whistle apropos',
  defaultLocale: 'ha',
  fallbackLocale: 'wetly exactly woeful',
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
