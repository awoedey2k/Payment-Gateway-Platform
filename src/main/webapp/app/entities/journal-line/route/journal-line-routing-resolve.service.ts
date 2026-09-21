import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IJournalLine } from '../journal-line.model';
import { JournalLineService } from '../service/journal-line.service';

const journalLineResolve = (route: ActivatedRouteSnapshot): Observable<null | IJournalLine> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(JournalLineService);
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

export default journalLineResolve;
