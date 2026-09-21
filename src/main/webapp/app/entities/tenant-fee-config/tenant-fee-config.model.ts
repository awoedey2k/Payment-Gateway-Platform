import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { ICountryPaymentMethod } from 'app/entities/country-payment-method/country-payment-method.model';
import { FeeBearer } from 'app/entities/enumerations/fee-bearer.model';

export interface ITenantFeeConfig {
  id: number;
  fixedFee?: number | null;
  percentageFee?: number | null;
  capAmount?: number | null;
  feeBearer?: keyof typeof FeeBearer | null;
  isActive?: boolean | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
  countryPaymentMethod?: Pick<ICountryPaymentMethod, 'id'> | null;
}

export type NewTenantFeeConfig = Omit<ITenantFeeConfig, 'id'> & { id: null };
