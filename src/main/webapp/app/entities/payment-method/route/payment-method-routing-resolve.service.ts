import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPaymentMethod } from '../payment-method.model';
import { PaymentMethodService } from '../service/payment-method.service';

const paymentMethodResolve = (route: ActivatedRouteSnapshot): Observable<null | IPaymentMethod> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PaymentMethodService);
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

export default paymentMethodResolve;
