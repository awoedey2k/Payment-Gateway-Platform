import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ICountryPaymentMethod, NewCountryPaymentMethod } from '../country-payment-method.model';

export type PartialUpdateCountryPaymentMethod = Partial<ICountryPaymentMethod> & Pick<ICountryPaymentMethod, 'id'>;

@Service()
export class CountryPaymentMethodsService {
  readonly countryPaymentMethodsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly countryPaymentMethodsResource = httpResource<ICountryPaymentMethod[]>(() => {
    const params = this.countryPaymentMethodsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of countryPaymentMethod that have been fetched. It is updated when the countryPaymentMethodsResource emits a new value.
   * In case of error while fetching the countryPaymentMethods, the signal is set to an empty array.
   */
  readonly countryPaymentMethods = computed(() =>
    this.countryPaymentMethodsResource.hasValue() ? this.countryPaymentMethodsResource.value() : [],
  );
  protected readonly resourceUrl = `${serverApiUrl}api/country-payment-methods`;
}

@Service()
export class CountryPaymentMethodService extends CountryPaymentMethodsService {
  protected readonly http = inject(HttpClient);

  create(countryPaymentMethod: NewCountryPaymentMethod): Observable<ICountryPaymentMethod> {
    return this.http.post<ICountryPaymentMethod>(this.resourceUrl, countryPaymentMethod);
  }

  update(countryPaymentMethod: ICountryPaymentMethod): Observable<ICountryPaymentMethod> {
    return this.http.put<ICountryPaymentMethod>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCountryPaymentMethodIdentifier(countryPaymentMethod))}`,
      countryPaymentMethod,
    );
  }

  partialUpdate(countryPaymentMethod: PartialUpdateCountryPaymentMethod): Observable<ICountryPaymentMethod> {
    return this.http.patch<ICountryPaymentMethod>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCountryPaymentMethodIdentifier(countryPaymentMethod))}`,
      countryPaymentMethod,
    );
  }

  find(id: number): Observable<ICountryPaymentMethod> {
    return this.http.get<ICountryPaymentMethod>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICountryPaymentMethod[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICountryPaymentMethod[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCountryPaymentMethodIdentifier(countryPaymentMethod: Pick<ICountryPaymentMethod, 'id'>): number {
    return countryPaymentMethod.id;
  }

  compareCountryPaymentMethod(o1: Pick<ICountryPaymentMethod, 'id'> | null, o2: Pick<ICountryPaymentMethod, 'id'> | null): boolean {
    return o1 && o2 ? this.getCountryPaymentMethodIdentifier(o1) === this.getCountryPaymentMethodIdentifier(o2) : o1 === o2;
  }

  addCountryPaymentMethodToCollectionIfMissing<Type extends Pick<ICountryPaymentMethod, 'id'>>(
    countryPaymentMethodCollection: Type[],
    ...countryPaymentMethodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const countryPaymentMethods: Type[] = countryPaymentMethodsToCheck.filter(
      countryPaymentMethodItem => countryPaymentMethodItem !== null && countryPaymentMethodItem !== undefined,
    );
    if (countryPaymentMethods.length > 0) {
      const countryPaymentMethodCollectionIdentifiers = countryPaymentMethodCollection.map(countryPaymentMethodItem =>
        this.getCountryPaymentMethodIdentifier(countryPaymentMethodItem),
      );
      const countryPaymentMethodsToAdd = countryPaymentMethods.filter(countryPaymentMethodItem => {
        const countryPaymentMethodIdentifier = this.getCountryPaymentMethodIdentifier(countryPaymentMethodItem);
        if (countryPaymentMethodCollectionIdentifiers.includes(countryPaymentMethodIdentifier)) {
          return false;
        }
        countryPaymentMethodCollectionIdentifiers.push(countryPaymentMethodIdentifier);
        return true;
      });
      return [...countryPaymentMethodsToAdd, ...countryPaymentMethodCollection];
    }
    return countryPaymentMethodCollection;
  }
}
