import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import RefundResolve from './route/refund-routing-resolve.service';

const refundRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/refund').then(m => m.Refund),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/refund-detail').then(m => m.RefundDetail),
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/refund-update').then(m => m.RefundUpdate),
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/refund-update').then(m => m.RefundUpdate),
    resolve: {
      refund: RefundResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default refundRoute;
