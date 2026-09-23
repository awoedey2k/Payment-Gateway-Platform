import { ICountryPaymentMethod, NewCountryPaymentMethod } from './country-payment-method.model';

export const sampleWithRequiredData: ICountryPaymentMethod = {
  id: 22862,
  minTxnAmount: 20240.34,
  maxTxnAmount: 23243.83,
  supportsRecurring: true,
  supportsInstantRefund: false,
  isActive: true,
};

export const sampleWithPartialData: ICountryPaymentMethod = {
  id: 30453,
  minTxnAmount: 4687.44,
  maxTxnAmount: 32299.28,
  supportsRecurring: false,
  supportsInstantRefund: false,
  isActive: false,
};

export const sampleWithFullData: ICountryPaymentMethod = {
  id: 8015,
  minTxnAmount: 15205.85,
  maxTxnAmount: 32313.21,
  supportsRecurring: false,
  supportsInstantRefund: false,
  isActive: false,
};

export const sampleWithNewData: NewCountryPaymentMethod = {
  minTxnAmount: 5914.79,
  maxTxnAmount: 11562.86,
  supportsRecurring: true,
  supportsInstantRefund: false,
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
