import dayjs from 'dayjs/esm';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { ApiEnvironment } from 'app/entities/enumerations/api-environment.model';

export interface IApiKey {
  id: number;
  keyPrefix?: string | null;
  keyHash?: string | null;
  environment?: keyof typeof ApiEnvironment | null;
  isActive?: boolean | null;
  issuedAt?: dayjs.Dayjs | null;
  revokedAt?: dayjs.Dayjs | null;
  graceExpiresAt?: dayjs.Dayjs | null;
  tenant?: Pick<ICorporateTenant, 'id'> | null;
}

export type NewApiKey = Omit<IApiKey, 'id'> & { id: null };
