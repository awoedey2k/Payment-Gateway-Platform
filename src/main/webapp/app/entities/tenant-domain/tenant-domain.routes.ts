import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TenantDomainResolve from './route/tenant-domain-routing-resolve.service';

const tenantDomainRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tenant-domain').then(m => m.TenantDomain),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tenant-domain-detail').then(m => m.TenantDomainDetail),
    resolve: {
      tenantDomain: TenantDomainResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tenant-domain-update').then(m => m.TenantDomainUpdate),
    resolve: {
      tenantDomain: TenantDomainResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tenant-domain-update').then(m => m.TenantDomainUpdate),
    resolve: {
      tenantDomain: TenantDomainResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default tenantDomainRoute;
