import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ForexRateResolve from './route/forex-rate-routing-resolve.service';

const forexRateRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/forex-rate').then(m => m.ForexRate),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/forex-rate-detail').then(m => m.ForexRateDetail),
    resolve: {
      forexRate: ForexRateResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/forex-rate-update').then(m => m.ForexRateUpdate),
    resolve: {
      forexRate: ForexRateResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/forex-rate-update').then(m => m.ForexRateUpdate),
    resolve: {
      forexRate: ForexRateResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default forexRateRoute;
