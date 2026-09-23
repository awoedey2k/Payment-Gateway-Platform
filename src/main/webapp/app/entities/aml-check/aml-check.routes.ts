import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AmlCheckResolve from './route/aml-check-routing-resolve.service';

const amlCheckRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/aml-check').then(m => m.AmlCheck),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/aml-check-detail').then(m => m.AmlCheckDetail),
    resolve: {
      amlCheck: AmlCheckResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/aml-check-update').then(m => m.AmlCheckUpdate),
    resolve: {
      amlCheck: AmlCheckResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/aml-check-update').then(m => m.AmlCheckUpdate),
    resolve: {
      amlCheck: AmlCheckResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default amlCheckRoute;
