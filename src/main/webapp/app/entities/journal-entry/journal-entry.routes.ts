import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import JournalEntryResolve from './route/journal-entry-routing-resolve.service';

const journalEntryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/journal-entry').then(m => m.JournalEntry),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/journal-entry-detail').then(m => m.JournalEntryDetail),
    resolve: {
      journalEntry: JournalEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/journal-entry-update').then(m => m.JournalEntryUpdate),
    resolve: {
      journalEntry: JournalEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/journal-entry-update').then(m => m.JournalEntryUpdate),
    resolve: {
      journalEntry: JournalEntryResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default journalEntryRoute;
