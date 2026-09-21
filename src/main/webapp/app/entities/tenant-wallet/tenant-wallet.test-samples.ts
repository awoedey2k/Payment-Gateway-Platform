import { ITenantWallet, NewTenantWallet } from './tenant-wallet.model';

export const sampleWithRequiredData: ITenantWallet = {
  id: 13319,
  currencyCode: 'DJF',
  availableBalance: 12602.59,
  lockedBalance: 23729.61,
};

export const sampleWithPartialData: ITenantWallet = {
  id: 24374,
  currencyCode: 'SSP',
  availableBalance: 30595.11,
  lockedBalance: 14611.55,
};

export const sampleWithFullData: ITenantWallet = {
  id: 26978,
  currencyCode: 'UAH',
  availableBalance: 3531.56,
  lockedBalance: 14721.44,
};

export const sampleWithNewData: NewTenantWallet = {
  currencyCode: 'KRW',
  availableBalance: 7031.88,
  lockedBalance: 21534.3,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
