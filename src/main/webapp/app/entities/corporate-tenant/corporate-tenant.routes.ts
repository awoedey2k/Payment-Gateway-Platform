import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import CorporateTenantResolve from './route/corporate-tenant-routing-resolve.service';

const corporateTenantRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/corporate-tenant').then(m => m.CorporateTenant),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/corporate-tenant-detail').then(m => m.CorporateTenantDetail),
    resolve: {
      corporateTenant: CorporateTenantResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/corporate-tenant-update').then(m => m.CorporateTenantUpdate),
    resolve: {
      corporateTenant: CorporateTenantResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/corporate-tenant-update').then(m => m.CorporateTenantUpdate),
    resolve: {
      corporateTenant: CorporateTenantResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default corporateTenantRoute;
