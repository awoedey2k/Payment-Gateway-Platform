import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import DisputeEvidenceResolve from './route/dispute-evidence-routing-resolve.service';

const disputeEvidenceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dispute-evidence').then(m => m.DisputeEvidence),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dispute-evidence-detail').then(m => m.DisputeEvidenceDetail),
    resolve: {
      disputeEvidence: DisputeEvidenceResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dispute-evidence-update').then(m => m.DisputeEvidenceUpdate),
    resolve: {
      disputeEvidence: DisputeEvidenceResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dispute-evidence-update').then(m => m.DisputeEvidenceUpdate),
    resolve: {
      disputeEvidence: DisputeEvidenceResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default disputeEvidenceRoute;
