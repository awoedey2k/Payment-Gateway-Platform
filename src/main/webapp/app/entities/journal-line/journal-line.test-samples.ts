import { IJournalLine, NewJournalLine } from './journal-line.model';

export const sampleWithRequiredData: IJournalLine = {
  id: 31389,
};

export const sampleWithPartialData: IJournalLine = {
  id: 5604,
  creditAmount: 13435.58,
};

export const sampleWithFullData: IJournalLine = {
  id: 31401,
  debitAmount: 356.05,
  creditAmount: 6267.2,
};

export const sampleWithNewData: NewJournalLine = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
