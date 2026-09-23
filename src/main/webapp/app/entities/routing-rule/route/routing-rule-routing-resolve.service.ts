import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IRoutingRule } from '../routing-rule.model';
import { RoutingRuleService } from '../service/routing-rule.service';

const routingRuleResolve = (route: ActivatedRouteSnapshot): Observable<null | IRoutingRule> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(RoutingRuleService);
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

export default routingRuleResolve;
