import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AuditLogEntryResolve from './route/audit-log-entry-routing-resolve.service';

const auditLogEntryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/audit-log-entry').then(m => m.AuditLogEntry),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/audit-log-entry-detail').then(m => m.AuditLogEntryDetail),
    resolve: {
      auditLogEntry: AuditLogEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/audit-log-entry-update').then(m => m.AuditLogEntryUpdate),
    resolve: {
      auditLogEntry: AuditLogEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/audit-log-entry-update').then(m => m.AuditLogEntryUpdate),
    resolve: {
      auditLogEntry: AuditLogEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default auditLogEntryRoute;
