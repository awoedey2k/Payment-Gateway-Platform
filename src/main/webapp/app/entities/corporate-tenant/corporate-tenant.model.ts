import dayjs from 'dayjs/esm';

import { CountryCode } from 'app/entities/enumerations/country-code.model';
import { KycStatus } from 'app/entities/enumerations/kyc-status.model';
import { TenantStatus } from 'app/entities/enumerations/tenant-status.model';

export interface ICorporateTenant {
  id: number;
  legalBusinessName?: string | null;
  businessRegistrationNumber?: string | null;
  taxIdentificationNumber?: string | null;
  operatingJurisdiction?: keyof typeof CountryCode | null;
  status?: keyof typeof TenantStatus | null;
  kycStatus?: keyof typeof KycStatus | null;
  riskScore?: number | null;
  createdAt?: dayjs.Dayjs | null;
  activatedAt?: dayjs.Dayjs | null;
}

export type NewCorporateTenant = Omit<ICorporateTenant, 'id'> & { id: null };
