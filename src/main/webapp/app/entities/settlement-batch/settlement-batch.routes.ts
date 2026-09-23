import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import SettlementBatchResolve from './route/settlement-batch-routing-resolve.service';

const settlementBatchRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/settlement-batch').then(m => m.SettlementBatch),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/settlement-batch-detail').then(m => m.SettlementBatchDetail),
    resolve: {
      settlementBatch: SettlementBatchResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/settlement-batch-update').then(m => m.SettlementBatchUpdate),
    resolve: {
      settlementBatch: SettlementBatchResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/settlement-batch-update').then(m => m.SettlementBatchUpdate),
    resolve: {
      settlementBatch: SettlementBatchResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default settlementBatchRoute;
