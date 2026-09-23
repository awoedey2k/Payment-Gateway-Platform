import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { WebhookSubscriptionService } from '../service/webhook-subscription.service';
import { IWebhookSubscription } from '../webhook-subscription.model';

const webhookSubscriptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IWebhookSubscription> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(WebhookSubscriptionService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default webhookSubscriptionResolve;
