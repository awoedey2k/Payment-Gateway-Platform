import { IRoutingRule, NewRoutingRule } from './routing-rule.model';

export const sampleWithRequiredData: IRoutingRule = {
  id: 2472,
  priority: 17511,
  scope: 'TENANT_CUSTOM',
  primaryAdapter: 'early',
  maxRetries: 30010,
  isActive: false,
};

export const sampleWithPartialData: IRoutingRule = {
  id: 8212,
  priority: 14996,
  scope: 'TENANT_CUSTOM',
  currencyCode: 'CVE',
  primaryAdapter: 'yahoo',
  fallbackAdapter: 'reproachfully',
  maxRetries: 13165,
  isActive: true,
};

export const sampleWithFullData: IRoutingRule = {
  id: 32378,
  priority: 11477,
  scope: 'TENANT_CUSTOM',
  countryCode: 'NL',
  currencyCode: 'QAR',
  cardBrand: 'appropriate scared',
  primaryAdapter: 'but new',
  fallbackAdapter: 'teeming frugal',
  maxRetries: 20914,
  isActive: true,
};

export const sampleWithNewData: NewRoutingRule = {
  priority: 24833,
  scope: 'TENANT_CUSTOM',
  primaryAdapter: 'forager',
  maxRetries: 27392,
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
