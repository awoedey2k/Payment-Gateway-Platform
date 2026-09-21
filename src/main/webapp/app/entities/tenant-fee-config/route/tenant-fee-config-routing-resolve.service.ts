import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TenantFeeConfigService } from '../service/tenant-fee-config.service';
import { ITenantFeeConfig } from '../tenant-fee-config.model';

const tenantFeeConfigResolve = (route: ActivatedRouteSnapshot): Observable<null | ITenantFeeConfig> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TenantFeeConfigService);
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

export default tenantFeeConfigResolve;
