import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import CountryPaymentMethodResolve from './route/country-payment-method-routing-resolve.service';

const countryPaymentMethodRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/country-payment-method').then(m => m.CountryPaymentMethod),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/country-payment-method-detail').then(m => m.CountryPaymentMethodDetail),
    resolve: {
      countryPaymentMethod: CountryPaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/country-payment-method-update').then(m => m.CountryPaymentMethodUpdate),
    resolve: {
      countryPaymentMethod: CountryPaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/country-payment-method-update').then(m => m.CountryPaymentMethodUpdate),
    resolve: {
      countryPaymentMethod: CountryPaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default countryPaymentMethodRoute;
