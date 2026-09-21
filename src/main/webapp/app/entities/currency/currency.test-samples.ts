import { ICurrency, NewCurrency } from './currency.model';

export const sampleWithRequiredData: ICurrency = {
  id: 28187,
  code: 'inc',
  name: 'knottily',
  minorUnit: 29295,
};

export const sampleWithPartialData: ICurrency = {
  id: 12245,
  code: 'bir',
  name: 'oh psst',
  minorUnit: 1285,
};

export const sampleWithFullData: ICurrency = {
  id: 30512,
  code: 'ser',
  name: 'far enfold',
  minorUnit: 28677,
};

export const sampleWithNewData: NewCurrency = {
  code: 'dra',
  name: 'whose absent ambitious',
  minorUnit: 20213,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
