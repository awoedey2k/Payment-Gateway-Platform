import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { WebhookDeliveryAttemptService } from '../service/webhook-delivery-attempt.service';
import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

const webhookDeliveryAttemptResolve = (route: ActivatedRouteSnapshot): Observable<null | IWebhookDeliveryAttempt> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(WebhookDeliveryAttemptService);
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

export default webhookDeliveryAttemptResolve;
