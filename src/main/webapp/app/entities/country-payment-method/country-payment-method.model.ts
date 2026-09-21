import { ICountry } from 'app/entities/country/country.model';
import { IPaymentMethod } from 'app/entities/payment-method/payment-method.model';

export interface ICountryPaymentMethod {
  id: number;
  minTxnAmount?: number | null;
  maxTxnAmount?: number | null;
  supportsRecurring?: boolean | null;
  supportsInstantRefund?: boolean | null;
  isActive?: boolean | null;
  country?: Pick<ICountry, 'id'> | null;
  paymentMethod?: Pick<IPaymentMethod, 'id'> | null;
}

export type NewCountryPaymentMethod = Omit<ICountryPaymentMethod, 'id'> & { id: null };
