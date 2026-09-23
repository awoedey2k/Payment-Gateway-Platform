export interface ICountry {
  id: number;
  isoCode?: string | null;
  name?: string | null;
  defaultCurrencyCode?: string | null;
}

export type NewCountry = Omit<ICountry, 'id'> & { id: null };
