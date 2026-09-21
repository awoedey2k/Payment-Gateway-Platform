import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT, serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITenantDirector, NewTenantDirector } from '../tenant-director.model';

export type PartialUpdateTenantDirector = Partial<ITenantDirector> & Pick<ITenantDirector, 'id'>;

type RestOf<T extends ITenantDirector | NewTenantDirector> = Omit<T, 'dateOfBirth'> & {
  dateOfBirth?: string | null;
};

export type RestTenantDirector = RestOf<ITenantDirector>;

export type NewRestTenantDirector = RestOf<NewTenantDirector>;

export type PartialUpdateRestTenantDirector = RestOf<PartialUpdateTenantDirector>;

@Service()
export class TenantDirectorsService {
  readonly tenantDirectorsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tenantDirectorsResource = httpResource<RestTenantDirector[]>(() => {
    const params = this.tenantDirectorsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tenantDirector that have been fetched. It is updated when the tenantDirectorsResource emits a new value.
   * In case of error while fetching the tenantDirectors, the signal is set to an empty array.
   */
  readonly tenantDirectors = computed(() =>
    (this.tenantDirectorsResource.hasValue() ? this.tenantDirectorsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/tenant-directors`;

  protected convertValueFromServer(restTenantDirector: RestTenantDirector): ITenantDirector {
    return {
      ...restTenantDirector,
      dateOfBirth: restTenantDirector.dateOfBirth ? dayjs(restTenantDirector.dateOfBirth) : undefined,
    };
  }
}

@Service()
export class TenantDirectorService extends TenantDirectorsService {
  protected readonly http = inject(HttpClient);

  create(tenantDirector: NewTenantDirector): Observable<ITenantDirector> {
    const copy = this.convertValueFromClient(tenantDirector);
    return this.http.post<RestTenantDirector>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tenantDirector: ITenantDirector): Observable<ITenantDirector> {
    const copy = this.convertValueFromClient(tenantDirector);
    return this.http
      .put<RestTenantDirector>(`${this.resourceUrl}/${encodeURIComponent(this.getTenantDirectorIdentifier(tenantDirector))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tenantDirector: PartialUpdateTenantDirector): Observable<ITenantDirector> {
    const copy = this.convertValueFromClient(tenantDirector);
    return this.http
      .patch<RestTenantDirector>(`${this.resourceUrl}/${encodeURIComponent(this.getTenantDirectorIdentifier(tenantDirector))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITenantDirector> {
    return this.http
      .get<RestTenantDirector>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITenantDirector[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTenantDirector[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTenantDirectorIdentifier(tenantDirector: Pick<ITenantDirector, 'id'>): number {
    return tenantDirector.id;
  }

  compareTenantDirector(o1: Pick<ITenantDirector, 'id'> | null, o2: Pick<ITenantDirector, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantDirectorIdentifier(o1) === this.getTenantDirectorIdentifier(o2) : o1 === o2;
  }

  addTenantDirectorToCollectionIfMissing<Type extends Pick<ITenantDirector, 'id'>>(
    tenantDirectorCollection: Type[],
    ...tenantDirectorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenantDirectors: Type[] = tenantDirectorsToCheck.filter(
      tenantDirectorItem => tenantDirectorItem !== null && tenantDirectorItem !== undefined,
    );
    if (tenantDirectors.length > 0) {
      const tenantDirectorCollectionIdentifiers = tenantDirectorCollection.map(tenantDirectorItem =>
        this.getTenantDirectorIdentifier(tenantDirectorItem),
      );
      const tenantDirectorsToAdd = tenantDirectors.filter(tenantDirectorItem => {
        const tenantDirectorIdentifier = this.getTenantDirectorIdentifier(tenantDirectorItem);
        if (tenantDirectorCollectionIdentifiers.includes(tenantDirectorIdentifier)) {
          return false;
        }
        tenantDirectorCollectionIdentifiers.push(tenantDirectorIdentifier);
        return true;
      });
      return [...tenantDirectorsToAdd, ...tenantDirectorCollection];
    }
    return tenantDirectorCollection;
  }

  protected convertValueFromClient<T extends ITenantDirector | NewTenantDirector | PartialUpdateTenantDirector>(
    tenantDirector: T,
  ): RestOf<T> {
    return {
      ...tenantDirector,
      dateOfBirth: tenantDirector.dateOfBirth?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTenantDirector): ITenantDirector {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTenantDirector[]): ITenantDirector[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
