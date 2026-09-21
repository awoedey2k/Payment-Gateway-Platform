import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICountryPaymentMethod } from '../country-payment-method.model';
import { CountryPaymentMethodService } from '../service/country-payment-method.service';

const countryPaymentMethodResolve = (route: ActivatedRouteSnapshot): Observable<null | ICountryPaymentMethod> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CountryPaymentMethodService);
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

export default countryPaymentMethodResolve;
