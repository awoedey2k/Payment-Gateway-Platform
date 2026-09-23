import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import DisputeResolve from './route/dispute-routing-resolve.service';

const disputeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dispute').then(m => m.Dispute),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dispute-detail').then(m => m.DisputeDetail),
    resolve: {
      dispute: DisputeResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dispute-update').then(m => m.DisputeUpdate),
    resolve: {
      dispute: DisputeResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dispute-update').then(m => m.DisputeUpdate),
    resolve: {
      dispute: DisputeResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default disputeRoute;
