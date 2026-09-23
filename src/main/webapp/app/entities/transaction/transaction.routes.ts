import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TransactionResolve from './route/transaction-routing-resolve.service';

const transactionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/transaction').then(m => m.Transaction),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/transaction-detail').then(m => m.TransactionDetail),
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/transaction-update').then(m => m.TransactionUpdate),
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/transaction-update').then(m => m.TransactionUpdate),
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default transactionRoute;
