import dayjs from 'dayjs/esm';

import { ActorType } from 'app/entities/enumerations/actor-type.model';

export interface IAuditLogEntry {
  id: number;
  actorType?: keyof typeof ActorType | null;
  actorId?: string | null;
  action?: string | null;
  entityType?: string | null;
  entityId?: string | null;
  previousHash?: string | null;
  entryHash?: string | null;
  recordedAt?: dayjs.Dayjs | null;
}

export type NewAuditLogEntry = Omit<IAuditLogEntry, 'id'> & { id: null };
