import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IAuditLogEntry } from '../audit-log-entry.model';
import { AuditLogEntryService } from '../service/audit-log-entry.service';

const auditLogEntryResolve = (route: ActivatedRouteSnapshot): Observable<null | IAuditLogEntry> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(AuditLogEntryService);
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

export default auditLogEntryResolve;
