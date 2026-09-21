import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import PaymentMethodResolve from './route/payment-method-routing-resolve.service';

const paymentMethodRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/payment-method').then(m => m.PaymentMethod),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/payment-method-detail').then(m => m.PaymentMethodDetail),
    resolve: {
      paymentMethod: PaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/payment-method-update').then(m => m.PaymentMethodUpdate),
    resolve: {
      paymentMethod: PaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/payment-method-update').then(m => m.PaymentMethodUpdate),
    resolve: {
      paymentMethod: PaymentMethodResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default paymentMethodRoute;
