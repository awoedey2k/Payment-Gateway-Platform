import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TenantWalletService } from '../service/tenant-wallet.service';
import { ITenantWallet } from '../tenant-wallet.model';

const tenantWalletResolve = (route: ActivatedRouteSnapshot): Observable<null | ITenantWallet> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TenantWalletService);
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

export default tenantWalletResolve;
