import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import JournalLineResolve from './route/journal-line-routing-resolve.service';

const journalLineRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/journal-line').then(m => m.JournalLine),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/journal-line-detail').then(m => m.JournalLineDetail),
    resolve: {
      journalLine: JournalLineResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/journal-line-update').then(m => m.JournalLineUpdate),
    resolve: {
      journalLine: JournalLineResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/journal-line-update').then(m => m.JournalLineUpdate),
    resolve: {
      journalLine: JournalLineResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default journalLineRoute;
