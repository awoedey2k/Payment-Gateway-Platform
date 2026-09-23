import { IPayoutSchedule, NewPayoutSchedule } from './payout-schedule.model';

export const sampleWithRequiredData: IPayoutSchedule = {
  id: 23798,
  frequencyMode: 'MANUAL',
  thresholdAmount: 16927.09,
  isActive: true,
};

export const sampleWithPartialData: IPayoutSchedule = {
  id: 28810,
  frequencyMode: 'DAILY',
  thresholdAmount: 11899.2,
  isActive: false,
};

export const sampleWithFullData: IPayoutSchedule = {
  id: 18328,
  frequencyMode: 'DAILY',
  thresholdAmount: 14882.21,
  isActive: true,
};

export const sampleWithNewData: NewPayoutSchedule = {
  frequencyMode: 'MONTHLY',
  thresholdAmount: 18800.95,
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
