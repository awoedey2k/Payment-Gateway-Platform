import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import PayoutScheduleResolve from './route/payout-schedule-routing-resolve.service';

const payoutScheduleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/payout-schedule').then(m => m.PayoutSchedule),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/payout-schedule-detail').then(m => m.PayoutScheduleDetail),
    resolve: {
      payoutSchedule: PayoutScheduleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/payout-schedule-update').then(m => m.PayoutScheduleUpdate),
    resolve: {
      payoutSchedule: PayoutScheduleResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/payout-schedule-update').then(m => m.PayoutScheduleUpdate),
    resolve: {
      payoutSchedule: PayoutScheduleResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default payoutScheduleRoute;
