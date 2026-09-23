import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IRoutingRule, NewRoutingRule } from '../routing-rule.model';

export type PartialUpdateRoutingRule = Partial<IRoutingRule> & Pick<IRoutingRule, 'id'>;

@Service()
export class RoutingRulesService {
  readonly routingRulesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly routingRulesResource = httpResource<IRoutingRule[]>(() => {
    const params = this.routingRulesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of routingRule that have been fetched. It is updated when the routingRulesResource emits a new value.
   * In case of error while fetching the routingRules, the signal is set to an empty array.
   */
  readonly routingRules = computed(() => (this.routingRulesResource.hasValue() ? this.routingRulesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/routing-rules`;
}

@Service()
export class RoutingRuleService extends RoutingRulesService {
  protected readonly http = inject(HttpClient);

  create(routingRule: NewRoutingRule): Observable<IRoutingRule> {
    return this.http.post<IRoutingRule>(this.resourceUrl, routingRule);
  }

  update(routingRule: IRoutingRule): Observable<IRoutingRule> {
    return this.http.put<IRoutingRule>(
      `${this.resourceUrl}/${encodeURIComponent(this.getRoutingRuleIdentifier(routingRule))}`,
      routingRule,
    );
  }

  partialUpdate(routingRule: PartialUpdateRoutingRule): Observable<IRoutingRule> {
    return this.http.patch<IRoutingRule>(
      `${this.resourceUrl}/${encodeURIComponent(this.getRoutingRuleIdentifier(routingRule))}`,
      routingRule,
    );
  }

  find(id: number): Observable<IRoutingRule> {
    return this.http.get<IRoutingRule>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IRoutingRule[]>> {
    const options = createRequestOption(req);
    return this.http.get<IRoutingRule[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRoutingRuleIdentifier(routingRule: Pick<IRoutingRule, 'id'>): number {
    return routingRule.id;
  }

  compareRoutingRule(o1: Pick<IRoutingRule, 'id'> | null, o2: Pick<IRoutingRule, 'id'> | null): boolean {
    return o1 && o2 ? this.getRoutingRuleIdentifier(o1) === this.getRoutingRuleIdentifier(o2) : o1 === o2;
  }

  addRoutingRuleToCollectionIfMissing<Type extends Pick<IRoutingRule, 'id'>>(
    routingRuleCollection: Type[],
    ...routingRulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const routingRules: Type[] = routingRulesToCheck.filter(routingRuleItem => routingRuleItem !== null && routingRuleItem !== undefined);
    if (routingRules.length > 0) {
      const routingRuleCollectionIdentifiers = routingRuleCollection.map(routingRuleItem => this.getRoutingRuleIdentifier(routingRuleItem));
      const routingRulesToAdd = routingRules.filter(routingRuleItem => {
        const routingRuleIdentifier = this.getRoutingRuleIdentifier(routingRuleItem);
        if (routingRuleCollectionIdentifiers.includes(routingRuleIdentifier)) {
          return false;
        }
        routingRuleCollectionIdentifiers.push(routingRuleIdentifier);
        return true;
      });
      return [...routingRulesToAdd, ...routingRuleCollection];
    }
    return routingRuleCollection;
  }
}
