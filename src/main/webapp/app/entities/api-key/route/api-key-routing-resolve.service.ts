import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

const apiKeyResolve = (route: ActivatedRouteSnapshot): Observable<null | IApiKey> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(ApiKeyService);
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

export default apiKeyResolve;
