import { ICountry, NewCountry } from './country.model';

export const sampleWithRequiredData: ICountry = {
  id: 30729,
  isoCode: 'de',
  name: 'sarcastic',
  defaultCurrencyCode: 'squirm outstanding',
};

export const sampleWithPartialData: ICountry = {
  id: 14020,
  isoCode: 'bo',
  name: 'towards',
  defaultCurrencyCode: 'behind',
};

export const sampleWithFullData: ICountry = {
  id: 28151,
  isoCode: 'fo',
  name: 'mainstream',
  defaultCurrencyCode: 'wedge enlightened',
};

export const sampleWithNewData: NewCountry = {
  isoCode: 'ab',
  name: 'exempt concerning what',
  defaultCurrencyCode: 'ugh overcharge',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
