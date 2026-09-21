import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IAmlCheck } from '../aml-check.model';
import { AmlCheckService } from '../service/aml-check.service';

const amlCheckResolve = (route: ActivatedRouteSnapshot): Observable<null | IAmlCheck> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(AmlCheckService);
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

export default amlCheckResolve;
