import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import CurrencyResolve from './route/currency-routing-resolve.service';

const currencyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/currency').then(m => m.Currency),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/currency-detail').then(m => m.CurrencyDetail),
    resolve: {
      currency: CurrencyResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/currency-update').then(m => m.CurrencyUpdate),
    resolve: {
      currency: CurrencyResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/currency-update').then(m => m.CurrencyUpdate),
    resolve: {
      currency: CurrencyResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default currencyRoute;
