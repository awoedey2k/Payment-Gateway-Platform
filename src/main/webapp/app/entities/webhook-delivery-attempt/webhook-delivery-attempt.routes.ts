import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import WebhookDeliveryAttemptResolve from './route/webhook-delivery-attempt-routing-resolve.service';

const webhookDeliveryAttemptRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/webhook-delivery-attempt').then(m => m.WebhookDeliveryAttempt),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/webhook-delivery-attempt-detail').then(m => m.WebhookDeliveryAttemptDetail),
    resolve: {
      webhookDeliveryAttempt: WebhookDeliveryAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/webhook-delivery-attempt-update').then(m => m.WebhookDeliveryAttemptUpdate),
    resolve: {
      webhookDeliveryAttempt: WebhookDeliveryAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/webhook-delivery-attempt-update').then(m => m.WebhookDeliveryAttemptUpdate),
    resolve: {
      webhookDeliveryAttempt: WebhookDeliveryAttemptResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default webhookDeliveryAttemptRoute;
