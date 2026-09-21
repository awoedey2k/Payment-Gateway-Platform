import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TenantFeeConfigResolve from './route/tenant-fee-config-routing-resolve.service';

const tenantFeeConfigRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tenant-fee-config').then(m => m.TenantFeeConfig),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tenant-fee-config-detail').then(m => m.TenantFeeConfigDetail),
    resolve: {
      tenantFeeConfig: TenantFeeConfigResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tenant-fee-config-update').then(m => m.TenantFeeConfigUpdate),
    resolve: {
      tenantFeeConfig: TenantFeeConfigResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tenant-fee-config-update').then(m => m.TenantFeeConfigUpdate),
    resolve: {
      tenantFeeConfig: TenantFeeConfigResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default tenantFeeConfigRoute;
