import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TenantDirectorService } from '../service/tenant-director.service';
import { ITenantDirector } from '../tenant-director.model';

const tenantDirectorResolve = (route: ActivatedRouteSnapshot): Observable<null | ITenantDirector> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TenantDirectorService);
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

export default tenantDirectorResolve;
