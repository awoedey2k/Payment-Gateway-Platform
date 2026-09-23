import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import WebhookSubscriptionResolve from './route/webhook-subscription-routing-resolve.service';

const webhookSubscriptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/webhook-subscription').then(m => m.WebhookSubscription),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/webhook-subscription-detail').then(m => m.WebhookSubscriptionDetail),
    resolve: {
      webhookSubscription: WebhookSubscriptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/webhook-subscription-update').then(m => m.WebhookSubscriptionUpdate),
    resolve: {
      webhookSubscription: WebhookSubscriptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/webhook-subscription-update').then(m => m.WebhookSubscriptionUpdate),
    resolve: {
      webhookSubscription: WebhookSubscriptionResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default webhookSubscriptionRoute;
