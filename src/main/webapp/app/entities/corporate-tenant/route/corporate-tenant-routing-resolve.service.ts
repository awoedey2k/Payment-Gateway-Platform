import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICorporateTenant } from '../corporate-tenant.model';
import { CorporateTenantService } from '../service/corporate-tenant.service';

const corporateTenantResolve = (route: ActivatedRouteSnapshot): Observable<null | ICorporateTenant> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CorporateTenantService);
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

export default corporateTenantResolve;
