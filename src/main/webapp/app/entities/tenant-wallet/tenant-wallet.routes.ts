import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import TenantWalletResolve from './route/tenant-wallet-routing-resolve.service';

const tenantWalletRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/tenant-wallet').then(m => m.TenantWallet),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/tenant-wallet-detail').then(m => m.TenantWalletDetail),
    resolve: {
      tenantWallet: TenantWalletResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/tenant-wallet-update').then(m => m.TenantWalletUpdate),
    resolve: {
      tenantWallet: TenantWalletResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/tenant-wallet-update').then(m => m.TenantWalletUpdate),
    resolve: {
      tenantWallet: TenantWalletResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default tenantWalletRoute;
