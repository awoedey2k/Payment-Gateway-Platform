import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import RoutingRuleResolve from './route/routing-rule-routing-resolve.service';

const routingRuleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/routing-rule').then(m => m.RoutingRule),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/routing-rule-detail').then(m => m.RoutingRuleDetail),
    resolve: {
      routingRule: RoutingRuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/routing-rule-update').then(m => m.RoutingRuleUpdate),
    resolve: {
      routingRule: RoutingRuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/routing-rule-update').then(m => m.RoutingRuleUpdate),
    resolve: {
      routingRule: RoutingRuleResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default routingRuleRoute;
