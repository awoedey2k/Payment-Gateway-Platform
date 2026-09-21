import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CountryCode } from 'app/entities/enumerations/country-code.model';
import { IdentificationType } from 'app/entities/enumerations/identification-type.model';

export interface ITenantDirector {
  id: number;
  fullName?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  nationality?: keyof typeof CountryCode | null;
  identificationType?: keyof typeof IdentificationType | null;
  identificationNumber?: string | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewTenantDirector = Omit<ITenantDirector, 'id'> & { id: null };
