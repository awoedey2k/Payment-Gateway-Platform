import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITenantDomain, NewTenantDomain } from '../tenant-domain.model';

export type PartialUpdateTenantDomain = Partial<ITenantDomain> & Pick<ITenantDomain, 'id'>;

@Service()
export class TenantDomainsService {
  readonly tenantDomainsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tenantDomainsResource = httpResource<ITenantDomain[]>(() => {
    const params = this.tenantDomainsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tenantDomain that have been fetched. It is updated when the tenantDomainsResource emits a new value.
   * In case of error while fetching the tenantDomains, the signal is set to an empty array.
   */
  readonly tenantDomains = computed(() => (this.tenantDomainsResource.hasValue() ? this.tenantDomainsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/tenant-domains`;
}

@Service()
export class TenantDomainService extends TenantDomainsService {
  protected readonly http = inject(HttpClient);

  create(tenantDomain: NewTenantDomain): Observable<ITenantDomain> {
    return this.http.post<ITenantDomain>(this.resourceUrl, tenantDomain);
  }

  update(tenantDomain: ITenantDomain): Observable<ITenantDomain> {
    return this.http.put<ITenantDomain>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantDomainIdentifier(tenantDomain))}`,
      tenantDomain,
    );
  }

  partialUpdate(tenantDomain: PartialUpdateTenantDomain): Observable<ITenantDomain> {
    return this.http.patch<ITenantDomain>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantDomainIdentifier(tenantDomain))}`,
      tenantDomain,
    );
  }

  find(id: number): Observable<ITenantDomain> {
    return this.http.get<ITenantDomain>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITenantDomain[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITenantDomain[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTenantDomainIdentifier(tenantDomain: Pick<ITenantDomain, 'id'>): number {
    return tenantDomain.id;
  }

  compareTenantDomain(o1: Pick<ITenantDomain, 'id'> | null, o2: Pick<ITenantDomain, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantDomainIdentifier(o1) === this.getTenantDomainIdentifier(o2) : o1 === o2;
  }

  addTenantDomainToCollectionIfMissing<Type extends Pick<ITenantDomain, 'id'>>(
    tenantDomainCollection: Type[],
    ...tenantDomainsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenantDomains: Type[] = tenantDomainsToCheck.filter(
      tenantDomainItem => tenantDomainItem !== null && tenantDomainItem !== undefined,
    );
    if (tenantDomains.length > 0) {
      const tenantDomainCollectionIdentifiers = tenantDomainCollection.map(tenantDomainItem =>
        this.getTenantDomainIdentifier(tenantDomainItem),
      );
      const tenantDomainsToAdd = tenantDomains.filter(tenantDomainItem => {
        const tenantDomainIdentifier = this.getTenantDomainIdentifier(tenantDomainItem);
        if (tenantDomainCollectionIdentifiers.includes(tenantDomainIdentifier)) {
          return false;
        }
        tenantDomainCollectionIdentifiers.push(tenantDomainIdentifier);
        return true;
      });
      return [...tenantDomainsToAdd, ...tenantDomainCollection];
    }
    return tenantDomainCollection;
  }
}
