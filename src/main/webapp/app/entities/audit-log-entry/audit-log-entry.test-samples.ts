import dayjs from 'dayjs/esm';

import { IAuditLogEntry, NewAuditLogEntry } from './audit-log-entry.model';

export const sampleWithRequiredData: IAuditLogEntry = {
  id: 3032,
  actorType: 'STAFF_USER',
  actorId: 'haze rudely proliferate',
  action: 'because absentmindedly unselfish',
  entityType: 'confiscate shabby adaptation',
  entityId: 'acquaintance',
  entryHash: 'requirement institutionalize excess',
  recordedAt: dayjs('2026-09-21T10:28'),
};

export const sampleWithPartialData: IAuditLogEntry = {
  id: 28190,
  actorType: 'STAFF_USER',
  actorId: 'repeatedly whose among',
  action: 'since unless',
  entityType: 'as cram',
  entityId: 'milestone equally phew',
  previousHash: 'finally near',
  entryHash: 'pillory storyboard',
  recordedAt: dayjs('2026-09-21T11:06'),
};

export const sampleWithFullData: IAuditLogEntry = {
  id: 28926,
  actorType: 'TENANT_USER',
  actorId: 'pfft',
  action: 'instead plus rebuke',
  entityType: 'guest validity',
  entityId: 'present swordfish gadzooks',
  previousHash: 'croon',
  entryHash: 'zowie whereas',
  recordedAt: dayjs('2026-09-20T20:19'),
};

export const sampleWithNewData: NewAuditLogEntry = {
  actorType: 'STAFF_USER',
  actorId: 'unless',
  action: 'quick',
  entityType: 'hmph vacantly sailor',
  entityId: 'which',
  entryHash: 'whose knavishly better',
  recordedAt: dayjs('2026-09-21T00:28'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
