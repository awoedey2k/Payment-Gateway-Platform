import { ILedgerAccount, NewLedgerAccount } from './ledger-account.model';

export const sampleWithRequiredData: ILedgerAccount = {
  id: 219,
  accountCode: 'hmph',
  accountType: 'ASSET',
  currencyCode: 'ALL',
};

export const sampleWithPartialData: ILedgerAccount = {
  id: 25915,
  accountCode: 'excluding',
  accountType: 'REVENUE',
  currencyCode: 'GIP',
};

export const sampleWithFullData: ILedgerAccount = {
  id: 25602,
  accountCode: 'suitcase',
  accountType: 'LIABILITY',
  currencyCode: 'MXN',
};

export const sampleWithNewData: NewLedgerAccount = {
  accountCode: 'meh than',
  accountType: 'LIABILITY',
  currencyCode: 'KWD',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
