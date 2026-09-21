import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ICurrency, NewCurrency } from '../currency.model';

export type PartialUpdateCurrency = Partial<ICurrency> & Pick<ICurrency, 'id'>;

@Service()
export class CurrenciesService {
  readonly currenciesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly currenciesResource = httpResource<ICurrency[]>(() => {
    const params = this.currenciesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of currency that have been fetched. It is updated when the currenciesResource emits a new value.
   * In case of error while fetching the currencies, the signal is set to an empty array.
   */
  readonly currencies = computed(() => (this.currenciesResource.hasValue() ? this.currenciesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/currencies`;
}

@Service()
export class CurrencyService extends CurrenciesService {
  protected readonly http = inject(HttpClient);

  create(currency: NewCurrency): Observable<ICurrency> {
    return this.http.post<ICurrency>(this.resourceUrl, currency);
  }

  update(currency: ICurrency): Observable<ICurrency> {
    return this.http.put<ICurrency>(`${this.resourceUrl}/${encodeURIComponent(this.getCurrencyIdentifier(currency))}`, currency);
  }

  partialUpdate(currency: PartialUpdateCurrency): Observable<ICurrency> {
    return this.http.patch<ICurrency>(`${this.resourceUrl}/${encodeURIComponent(this.getCurrencyIdentifier(currency))}`, currency);
  }

  find(id: number): Observable<ICurrency> {
    return this.http.get<ICurrency>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICurrency[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICurrency[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCurrencyIdentifier(currency: Pick<ICurrency, 'id'>): number {
    return currency.id;
  }

  compareCurrency(o1: Pick<ICurrency, 'id'> | null, o2: Pick<ICurrency, 'id'> | null): boolean {
    return o1 && o2 ? this.getCurrencyIdentifier(o1) === this.getCurrencyIdentifier(o2) : o1 === o2;
  }

  addCurrencyToCollectionIfMissing<Type extends Pick<ICurrency, 'id'>>(
    currencyCollection: Type[],
    ...currenciesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const currencies: Type[] = currenciesToCheck.filter(currencyItem => currencyItem !== null && currencyItem !== undefined);
    if (currencies.length > 0) {
      const currencyCollectionIdentifiers = currencyCollection.map(currencyItem => this.getCurrencyIdentifier(currencyItem));
      const currenciesToAdd = currencies.filter(currencyItem => {
        const currencyIdentifier = this.getCurrencyIdentifier(currencyItem);
        if (currencyCollectionIdentifiers.includes(currencyIdentifier)) {
          return false;
        }
        currencyCollectionIdentifiers.push(currencyIdentifier);
        return true;
      });
      return [...currenciesToAdd, ...currencyCollection];
    }
    return currencyCollection;
  }
}
