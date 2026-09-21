import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IAmlCheck, NewAmlCheck } from '../aml-check.model';

export type PartialUpdateAmlCheck = Partial<IAmlCheck> & Pick<IAmlCheck, 'id'>;

type RestOf<T extends IAmlCheck | NewAmlCheck> = Omit<T, 'checkedAt'> & {
  checkedAt?: string | null;
};

export type RestAmlCheck = RestOf<IAmlCheck>;

export type NewRestAmlCheck = RestOf<NewAmlCheck>;

export type PartialUpdateRestAmlCheck = RestOf<PartialUpdateAmlCheck>;

@Service()
export class AmlChecksService {
  readonly amlChecksParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly amlChecksResource = httpResource<RestAmlCheck[]>(() => {
    const params = this.amlChecksParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of amlCheck that have been fetched. It is updated when the amlChecksResource emits a new value.
   * In case of error while fetching the amlChecks, the signal is set to an empty array.
   */
  readonly amlChecks = computed(() =>
    (this.amlChecksResource.hasValue() ? this.amlChecksResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/aml-checks`;

  protected convertValueFromServer(restAmlCheck: RestAmlCheck): IAmlCheck {
    return {
      ...restAmlCheck,
      checkedAt: restAmlCheck.checkedAt ? dayjs(restAmlCheck.checkedAt) : undefined,
    };
  }
}

@Service()
export class AmlCheckService extends AmlChecksService {
  protected readonly http = inject(HttpClient);

  create(amlCheck: NewAmlCheck): Observable<IAmlCheck> {
    const copy = this.convertValueFromClient(amlCheck);
    return this.http.post<RestAmlCheck>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(amlCheck: IAmlCheck): Observable<IAmlCheck> {
    const copy = this.convertValueFromClient(amlCheck);
    return this.http
      .put<RestAmlCheck>(`${this.resourceUrl}/${encodeURIComponent(this.getAmlCheckIdentifier(amlCheck))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(amlCheck: PartialUpdateAmlCheck): Observable<IAmlCheck> {
    const copy = this.convertValueFromClient(amlCheck);
    return this.http
      .patch<RestAmlCheck>(`${this.resourceUrl}/${encodeURIComponent(this.getAmlCheckIdentifier(amlCheck))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAmlCheck> {
    return this.http
      .get<RestAmlCheck>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAmlCheck[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAmlCheck[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAmlCheckIdentifier(amlCheck: Pick<IAmlCheck, 'id'>): number {
    return amlCheck.id;
  }

  compareAmlCheck(o1: Pick<IAmlCheck, 'id'> | null, o2: Pick<IAmlCheck, 'id'> | null): boolean {
    return o1 && o2 ? this.getAmlCheckIdentifier(o1) === this.getAmlCheckIdentifier(o2) : o1 === o2;
  }

  addAmlCheckToCollectionIfMissing<Type extends Pick<IAmlCheck, 'id'>>(
    amlCheckCollection: Type[],
    ...amlChecksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const amlChecks: Type[] = amlChecksToCheck.filter(amlCheckItem => amlCheckItem !== null && amlCheckItem !== undefined);
    if (amlChecks.length > 0) {
      const amlCheckCollectionIdentifiers = amlCheckCollection.map(amlCheckItem => this.getAmlCheckIdentifier(amlCheckItem));
      const amlChecksToAdd = amlChecks.filter(amlCheckItem => {
        const amlCheckIdentifier = this.getAmlCheckIdentifier(amlCheckItem);
        if (amlCheckCollectionIdentifiers.includes(amlCheckIdentifier)) {
          return false;
        }
        amlCheckCollectionIdentifiers.push(amlCheckIdentifier);
        return true;
      });
      return [...amlChecksToAdd, ...amlCheckCollection];
    }
    return amlCheckCollection;
  }

  protected convertValueFromClient<T extends IAmlCheck | NewAmlCheck | PartialUpdateAmlCheck>(amlCheck: T): RestOf<T> {
    return {
      ...amlCheck,
      checkedAt: amlCheck.checkedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAmlCheck): IAmlCheck {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAmlCheck[]): IAmlCheck[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
