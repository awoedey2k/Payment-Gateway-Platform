import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ICorporateTenant, NewCorporateTenant } from '../corporate-tenant.model';

export type PartialUpdateCorporateTenant = Partial<ICorporateTenant> & Pick<ICorporateTenant, 'id'>;

type RestOf<T extends ICorporateTenant | NewCorporateTenant> = Omit<T, 'createdAt' | 'activatedAt'> & {
  createdAt?: string | null;
  activatedAt?: string | null;
};

export type RestCorporateTenant = RestOf<ICorporateTenant>;

export type NewRestCorporateTenant = RestOf<NewCorporateTenant>;

export type PartialUpdateRestCorporateTenant = RestOf<PartialUpdateCorporateTenant>;

@Service()
export class CorporateTenantsService {
  readonly corporateTenantsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly corporateTenantsResource = httpResource<RestCorporateTenant[]>(() => {
    const params = this.corporateTenantsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of corporateTenant that have been fetched. It is updated when the corporateTenantsResource emits a new value.
   * In case of error while fetching the corporateTenants, the signal is set to an empty array.
   */
  readonly corporateTenants = computed(() =>
    (this.corporateTenantsResource.hasValue() ? this.corporateTenantsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/corporate-tenants`;

  protected convertValueFromServer(restCorporateTenant: RestCorporateTenant): ICorporateTenant {
    return {
      ...restCorporateTenant,
      createdAt: restCorporateTenant.createdAt ? dayjs(restCorporateTenant.createdAt) : undefined,
      activatedAt: restCorporateTenant.activatedAt ? dayjs(restCorporateTenant.activatedAt) : undefined,
    };
  }
}

@Service()
export class CorporateTenantService extends CorporateTenantsService {
  protected readonly http = inject(HttpClient);

  create(corporateTenant: NewCorporateTenant): Observable<ICorporateTenant> {
    const copy = this.convertValueFromClient(corporateTenant);
    return this.http.post<RestCorporateTenant>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(corporateTenant: ICorporateTenant): Observable<ICorporateTenant> {
    const copy = this.convertValueFromClient(corporateTenant);
    return this.http
      .put<RestCorporateTenant>(`${this.resourceUrl}/${encodeURIComponent(this.getCorporateTenantIdentifier(corporateTenant))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(corporateTenant: PartialUpdateCorporateTenant): Observable<ICorporateTenant> {
    const copy = this.convertValueFromClient(corporateTenant);
    return this.http
      .patch<RestCorporateTenant>(`${this.resourceUrl}/${encodeURIComponent(this.getCorporateTenantIdentifier(corporateTenant))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICorporateTenant> {
    return this.http
      .get<RestCorporateTenant>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICorporateTenant[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCorporateTenant[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCorporateTenantIdentifier(corporateTenant: Pick<ICorporateTenant, 'id'>): number {
    return corporateTenant.id;
  }

  compareCorporateTenant(o1: Pick<ICorporateTenant, 'id'> | null, o2: Pick<ICorporateTenant, 'id'> | null): boolean {
    return o1 && o2 ? this.getCorporateTenantIdentifier(o1) === this.getCorporateTenantIdentifier(o2) : o1 === o2;
  }

  addCorporateTenantToCollectionIfMissing<Type extends Pick<ICorporateTenant, 'id'>>(
    corporateTenantCollection: Type[],
    ...corporateTenantsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const corporateTenants: Type[] = corporateTenantsToCheck.filter(
      corporateTenantItem => corporateTenantItem !== null && corporateTenantItem !== undefined,
    );
    if (corporateTenants.length > 0) {
      const corporateTenantCollectionIdentifiers = corporateTenantCollection.map(corporateTenantItem =>
        this.getCorporateTenantIdentifier(corporateTenantItem),
      );
      const corporateTenantsToAdd = corporateTenants.filter(corporateTenantItem => {
        const corporateTenantIdentifier = this.getCorporateTenantIdentifier(corporateTenantItem);
        if (corporateTenantCollectionIdentifiers.includes(corporateTenantIdentifier)) {
          return false;
        }
        corporateTenantCollectionIdentifiers.push(corporateTenantIdentifier);
        return true;
      });
      return [...corporateTenantsToAdd, ...corporateTenantCollection];
    }
    return corporateTenantCollection;
  }

  protected convertValueFromClient<T extends ICorporateTenant | NewCorporateTenant | PartialUpdateCorporateTenant>(
    corporateTenant: T,
  ): RestOf<T> {
    return {
      ...corporateTenant,
      createdAt: corporateTenant.createdAt?.toJSON() ?? null,
      activatedAt: corporateTenant.activatedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCorporateTenant): ICorporateTenant {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCorporateTenant[]): ICorporateTenant[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
