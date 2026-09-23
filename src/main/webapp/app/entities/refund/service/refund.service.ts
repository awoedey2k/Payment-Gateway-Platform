import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IRefund, NewRefund } from '../refund.model';

export type PartialUpdateRefund = Partial<IRefund> & Pick<IRefund, 'id'>;

type RestOf<T extends IRefund | NewRefund> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestRefund = RestOf<IRefund>;

export type NewRestRefund = RestOf<NewRefund>;

export type PartialUpdateRestRefund = RestOf<PartialUpdateRefund>;

@Service()
export class RefundsService {
  readonly refundsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly refundsResource = httpResource<RestRefund[]>(() => {
    const params = this.refundsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of refund that have been fetched. It is updated when the refundsResource emits a new value.
   * In case of error while fetching the refunds, the signal is set to an empty array.
   */
  readonly refunds = computed(() =>
    (this.refundsResource.hasValue() ? this.refundsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/refunds`;

  protected convertValueFromServer(restRefund: RestRefund): IRefund {
    return {
      ...restRefund,
      createdAt: restRefund.createdAt ? dayjs(restRefund.createdAt) : undefined,
    };
  }
}

@Service()
export class RefundService extends RefundsService {
  protected readonly http = inject(HttpClient);

  create(refund: NewRefund): Observable<IRefund> {
    const copy = this.convertValueFromClient(refund);
    return this.http.post<RestRefund>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(refund: IRefund): Observable<IRefund> {
    const copy = this.convertValueFromClient(refund);
    return this.http
      .put<RestRefund>(`${this.resourceUrl}/${encodeURIComponent(this.getRefundIdentifier(refund))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(refund: PartialUpdateRefund): Observable<IRefund> {
    const copy = this.convertValueFromClient(refund);
    return this.http
      .patch<RestRefund>(`${this.resourceUrl}/${encodeURIComponent(this.getRefundIdentifier(refund))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRefund> {
    return this.http.get<RestRefund>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRefund[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRefund[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRefundIdentifier(refund: Pick<IRefund, 'id'>): number {
    return refund.id;
  }

  compareRefund(o1: Pick<IRefund, 'id'> | null, o2: Pick<IRefund, 'id'> | null): boolean {
    return o1 && o2 ? this.getRefundIdentifier(o1) === this.getRefundIdentifier(o2) : o1 === o2;
  }

  addRefundToCollectionIfMissing<Type extends Pick<IRefund, 'id'>>(
    refundCollection: Type[],
    ...refundsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const refunds: Type[] = refundsToCheck.filter(refundItem => refundItem !== null && refundItem !== undefined);
    if (refunds.length > 0) {
      const refundCollectionIdentifiers = refundCollection.map(refundItem => this.getRefundIdentifier(refundItem));
      const refundsToAdd = refunds.filter(refundItem => {
        const refundIdentifier = this.getRefundIdentifier(refundItem);
        if (refundCollectionIdentifiers.includes(refundIdentifier)) {
          return false;
        }
        refundCollectionIdentifiers.push(refundIdentifier);
        return true;
      });
      return [...refundsToAdd, ...refundCollection];
    }
    return refundCollection;
  }

  protected convertValueFromClient<T extends IRefund | NewRefund | PartialUpdateRefund>(refund: T): RestOf<T> {
    return {
      ...refund,
      createdAt: refund.createdAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRefund): IRefund {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRefund[]): IRefund[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
