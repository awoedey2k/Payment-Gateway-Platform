import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import ApiKeyResolve from './route/api-key-routing-resolve.service';

const apiKeyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/api-key').then(m => m.ApiKey),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/api-key-detail').then(m => m.ApiKeyDetail),
    resolve: {
      apiKey: ApiKeyResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/api-key-update').then(m => m.ApiKeyUpdate),
    resolve: {
      apiKey: ApiKeyResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/api-key-update').then(m => m.ApiKeyUpdate),
    resolve: {
      apiKey: ApiKeyResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default apiKeyRoute;
