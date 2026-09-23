import { ITenantFeeConfig, NewTenantFeeConfig } from './tenant-fee-config.model';

export const sampleWithRequiredData: ITenantFeeConfig = {
  id: 9308,
  fixedFee: 31272.63,
  percentageFee: 22208.45,
  feeBearer: 'MERCHANT',
  isActive: true,
};

export const sampleWithPartialData: ITenantFeeConfig = {
  id: 21773,
  fixedFee: 26378.49,
  percentageFee: 17076.47,
  capAmount: 10912.56,
  feeBearer: 'CUSTOMER',
  isActive: true,
};

export const sampleWithFullData: ITenantFeeConfig = {
  id: 27637,
  fixedFee: 16614.62,
  percentageFee: 9918.54,
  capAmount: 25426.73,
  feeBearer: 'CUSTOMER',
  isActive: false,
};

export const sampleWithNewData: NewTenantFeeConfig = {
  fixedFee: 19059.03,
  percentageFee: 3857.73,
  feeBearer: 'CUSTOMER',
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
