import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPayoutSchedule } from '../payout-schedule.model';
import { PayoutScheduleService } from '../service/payout-schedule.service';

const payoutScheduleResolve = (route: ActivatedRouteSnapshot): Observable<null | IPayoutSchedule> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PayoutScheduleService);
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

export default payoutScheduleResolve;
