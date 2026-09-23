export interface ICurrency {
  id: number;
  code?: string | null;
  name?: string | null;
  minorUnit?: number | null;
}

export type NewCurrency = Omit<ICurrency, 'id'> & { id: null };
