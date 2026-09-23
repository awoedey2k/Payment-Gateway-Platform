import dayjs from 'dayjs/esm';

import { IJournalEntry, NewJournalEntry } from './journal-entry.model';

export const sampleWithRequiredData: IJournalEntry = {
  id: 2415,
  reference: 'fondly that submissive',
  description: 'preheat',
  postedAt: dayjs('2026-09-20T20:05'),
};

export const sampleWithPartialData: IJournalEntry = {
  id: 29420,
  reference: 'riser',
  description: 'a',
  postedAt: dayjs('2026-09-20T23:15'),
};

export const sampleWithFullData: IJournalEntry = {
  id: 8338,
  reference: 'cleverly progress mob',
  description: 'yuck wilderness bad',
  postedAt: dayjs('2026-09-21T04:04'),
};

export const sampleWithNewData: NewJournalEntry = {
  reference: 'ew by couch',
  description: 'each including',
  postedAt: dayjs('2026-09-21T06:30'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
