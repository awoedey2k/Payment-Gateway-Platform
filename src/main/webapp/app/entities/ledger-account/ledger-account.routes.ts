import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import LedgerAccountResolve from './route/ledger-account-routing-resolve.service';

const ledgerAccountRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ledger-account').then(m => m.LedgerAccount),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ledger-account-detail').then(m => m.LedgerAccountDetail),
    resolve: {
      ledgerAccount: LedgerAccountResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ledger-account-update').then(m => m.LedgerAccountUpdate),
    resolve: {
      ledgerAccount: LedgerAccountResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ledger-account-update').then(m => m.LedgerAccountUpdate),
    resolve: {
      ledgerAccount: LedgerAccountResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default ledgerAccountRoute;
