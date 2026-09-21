import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TenantDirectorResolve from './route/tenant-director-routing-resolve.service';

const tenantDirectorRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tenant-director').then(m => m.TenantDirector),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tenant-director-detail').then(m => m.TenantDirectorDetail),
    resolve: {
      tenantDirector: TenantDirectorResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tenant-director-update').then(m => m.TenantDirectorUpdate),
    resolve: {
      tenantDirector: TenantDirectorResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tenant-director-update').then(m => m.TenantDirectorUpdate),
    resolve: {
      tenantDirector: TenantDirectorResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default tenantDirectorRoute;
