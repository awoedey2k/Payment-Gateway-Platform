import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IPaymentMethod, NewPaymentMethod } from '../payment-method.model';

export type PartialUpdatePaymentMethod = Partial<IPaymentMethod> & Pick<IPaymentMethod, 'id'>;

@Service()
export class PaymentMethodsService {
  readonly paymentMethodsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly paymentMethodsResource = httpResource<IPaymentMethod[]>(() => {
    const params = this.paymentMethodsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of paymentMethod that have been fetched. It is updated when the paymentMethodsResource emits a new value.
   * In case of error while fetching the paymentMethods, the signal is set to an empty array.
   */
  readonly paymentMethods = computed(() => (this.paymentMethodsResource.hasValue() ? this.paymentMethodsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/payment-methods`;
}

@Service()
export class PaymentMethodService extends PaymentMethodsService {
  protected readonly http = inject(HttpClient);

  create(paymentMethod: NewPaymentMethod): Observable<IPaymentMethod> {
    return this.http.post<IPaymentMethod>(this.resourceUrl, paymentMethod);
  }

  update(paymentMethod: IPaymentMethod): Observable<IPaymentMethod> {
    return this.http.put<IPaymentMethod>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPaymentMethodIdentifier(paymentMethod))}`,
      paymentMethod,
    );
  }

  partialUpdate(paymentMethod: PartialUpdatePaymentMethod): Observable<IPaymentMethod> {
    return this.http.patch<IPaymentMethod>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPaymentMethodIdentifier(paymentMethod))}`,
      paymentMethod,
    );
  }

  find(id: number): Observable<IPaymentMethod> {
    return this.http.get<IPaymentMethod>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPaymentMethod[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPaymentMethod[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPaymentMethodIdentifier(paymentMethod: Pick<IPaymentMethod, 'id'>): number {
    return paymentMethod.id;
  }

  comparePaymentMethod(o1: Pick<IPaymentMethod, 'id'> | null, o2: Pick<IPaymentMethod, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentMethodIdentifier(o1) === this.getPaymentMethodIdentifier(o2) : o1 === o2;
  }

  addPaymentMethodToCollectionIfMissing<Type extends Pick<IPaymentMethod, 'id'>>(
    paymentMethodCollection: Type[],
    ...paymentMethodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paymentMethods: Type[] = paymentMethodsToCheck.filter(
      paymentMethodItem => paymentMethodItem !== null && paymentMethodItem !== undefined,
    );
    if (paymentMethods.length > 0) {
      const paymentMethodCollectionIdentifiers = paymentMethodCollection.map(paymentMethodItem =>
        this.getPaymentMethodIdentifier(paymentMethodItem),
      );
      const paymentMethodsToAdd = paymentMethods.filter(paymentMethodItem => {
        const paymentMethodIdentifier = this.getPaymentMethodIdentifier(paymentMethodItem);
        if (paymentMethodCollectionIdentifiers.includes(paymentMethodIdentifier)) {
          return false;
        }
        paymentMethodCollectionIdentifiers.push(paymentMethodIdentifier);
        return true;
      });
      return [...paymentMethodsToAdd, ...paymentMethodCollection];
    }
    return paymentMethodCollection;
  }
}
