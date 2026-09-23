import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ILedgerAccount } from '../ledger-account.model';
import { LedgerAccountService } from '../service/ledger-account.service';

const ledgerAccountResolve = (route: ActivatedRouteSnapshot): Observable<null | ILedgerAccount> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(LedgerAccountService);
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

export default ledgerAccountResolve;
