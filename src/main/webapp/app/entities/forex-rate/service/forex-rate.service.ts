import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IForexRate, NewForexRate } from '../forex-rate.model';

export type PartialUpdateForexRate = Partial<IForexRate> & Pick<IForexRate, 'id'>;

type RestOf<T extends IForexRate | NewForexRate> = Omit<T, 'lockedAt' | 'expiresAt'> & {
  lockedAt?: string | null;
  expiresAt?: string | null;
};

export type RestForexRate = RestOf<IForexRate>;

export type NewRestForexRate = RestOf<NewForexRate>;

export type PartialUpdateRestForexRate = RestOf<PartialUpdateForexRate>;

@Service()
export class ForexRatesService {
  readonly forexRatesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly forexRatesResource = httpResource<RestForexRate[]>(() => {
    const params = this.forexRatesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of forexRate that have been fetched. It is updated when the forexRatesResource emits a new value.
   * In case of error while fetching the forexRates, the signal is set to an empty array.
   */
  readonly forexRates = computed(() =>
    (this.forexRatesResource.hasValue() ? this.forexRatesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/forex-rates`;

  protected convertValueFromServer(restForexRate: RestForexRate): IForexRate {
    return {
      ...restForexRate,
      lockedAt: restForexRate.lockedAt ? dayjs(restForexRate.lockedAt) : undefined,
      expiresAt: restForexRate.expiresAt ? dayjs(restForexRate.expiresAt) : undefined,
    };
  }
}

@Service()
export class ForexRateService extends ForexRatesService {
  protected readonly http = inject(HttpClient);

  create(forexRate: NewForexRate): Observable<IForexRate> {
    const copy = this.convertValueFromClient(forexRate);
    return this.http.post<RestForexRate>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(forexRate: IForexRate): Observable<IForexRate> {
    const copy = this.convertValueFromClient(forexRate);
    return this.http
      .put<RestForexRate>(`${this.resourceUrl}/${encodeURIComponent(this.getForexRateIdentifier(forexRate))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(forexRate: PartialUpdateForexRate): Observable<IForexRate> {
    const copy = this.convertValueFromClient(forexRate);
    return this.http
      .patch<RestForexRate>(`${this.resourceUrl}/${encodeURIComponent(this.getForexRateIdentifier(forexRate))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IForexRate> {
    return this.http
      .get<RestForexRate>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IForexRate[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestForexRate[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getForexRateIdentifier(forexRate: Pick<IForexRate, 'id'>): number {
    return forexRate.id;
  }

  compareForexRate(o1: Pick<IForexRate, 'id'> | null, o2: Pick<IForexRate, 'id'> | null): boolean {
    return o1 && o2 ? this.getForexRateIdentifier(o1) === this.getForexRateIdentifier(o2) : o1 === o2;
  }

  addForexRateToCollectionIfMissing<Type extends Pick<IForexRate, 'id'>>(
    forexRateCollection: Type[],
    ...forexRatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const forexRates: Type[] = forexRatesToCheck.filter(forexRateItem => forexRateItem !== null && forexRateItem !== undefined);
    if (forexRates.length > 0) {
      const forexRateCollectionIdentifiers = forexRateCollection.map(forexRateItem => this.getForexRateIdentifier(forexRateItem));
      const forexRatesToAdd = forexRates.filter(forexRateItem => {
        const forexRateIdentifier = this.getForexRateIdentifier(forexRateItem);
        if (forexRateCollectionIdentifiers.includes(forexRateIdentifier)) {
          return false;
        }
        forexRateCollectionIdentifiers.push(forexRateIdentifier);
        return true;
      });
      return [...forexRatesToAdd, ...forexRateCollection];
    }
    return forexRateCollection;
  }

  protected convertValueFromClient<T extends IForexRate | NewForexRate | PartialUpdateForexRate>(forexRate: T): RestOf<T> {
    return {
      ...forexRate,
      lockedAt: forexRate.lockedAt?.toJSON() ?? null,
      expiresAt: forexRate.expiresAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestForexRate): IForexRate {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestForexRate[]): IForexRate[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
