import { IPaymentMethod, NewPaymentMethod } from './payment-method.model';

export const sampleWithRequiredData: IPaymentMethod = {
  id: 21285,
  code: 'yum',
  displayName: 'ha',
  category: 'USSD',
};

export const sampleWithPartialData: IPaymentMethod = {
  id: 32463,
  code: 'plus until',
  displayName: 'pacemaker christen',
  category: 'MOBILE_MONEY',
};

export const sampleWithFullData: IPaymentMethod = {
  id: 4693,
  code: 'nor unusual now',
  displayName: 'frightened continually',
  category: 'WALLET',
};

export const sampleWithNewData: NewPaymentMethod = {
  code: 'uh-huh well-made unlike',
  displayName: 'the schlep intelligent',
  category: 'BANK_TRANSFER',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
