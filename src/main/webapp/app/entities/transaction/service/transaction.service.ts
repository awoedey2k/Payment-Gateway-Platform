import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITransaction, NewTransaction } from '../transaction.model';

export type PartialUpdateTransaction = Partial<ITransaction> & Pick<ITransaction, 'id'>;

type RestOf<T extends ITransaction | NewTransaction> = Omit<T, 'createdAt' | 'completedAt'> & {
  createdAt?: string | null;
  completedAt?: string | null;
};

export type RestTransaction = RestOf<ITransaction>;

export type NewRestTransaction = RestOf<NewTransaction>;

export type PartialUpdateRestTransaction = RestOf<PartialUpdateTransaction>;

@Service()
export class TransactionsService {
  readonly transactionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly transactionsResource = httpResource<RestTransaction[]>(() => {
    const params = this.transactionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of transaction that have been fetched. It is updated when the transactionsResource emits a new value.
   * In case of error while fetching the transactions, the signal is set to an empty array.
   */
  readonly transactions = computed(() =>
    (this.transactionsResource.hasValue() ? this.transactionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/transactions`;

  protected convertValueFromServer(restTransaction: RestTransaction): ITransaction {
    return {
      ...restTransaction,
      createdAt: restTransaction.createdAt ? dayjs(restTransaction.createdAt) : undefined,
      completedAt: restTransaction.completedAt ? dayjs(restTransaction.completedAt) : undefined,
    };
  }
}

@Service()
export class TransactionService extends TransactionsService {
  protected readonly http = inject(HttpClient);

  create(transaction: NewTransaction): Observable<ITransaction> {
    const copy = this.convertValueFromClient(transaction);
    return this.http.post<RestTransaction>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(transaction: ITransaction): Observable<ITransaction> {
    const copy = this.convertValueFromClient(transaction);
    return this.http
      .put<RestTransaction>(`${this.resourceUrl}/${encodeURIComponent(this.getTransactionIdentifier(transaction))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(transaction: PartialUpdateTransaction): Observable<ITransaction> {
    const copy = this.convertValueFromClient(transaction);
    return this.http
      .patch<RestTransaction>(`${this.resourceUrl}/${encodeURIComponent(this.getTransactionIdentifier(transaction))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITransaction> {
    return this.http
      .get<RestTransaction>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ITransaction[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTransactionIdentifier(transaction: Pick<ITransaction, 'id'>): number {
    return transaction.id;
  }

  compareTransaction(o1: Pick<ITransaction, 'id'> | null, o2: Pick<ITransaction, 'id'> | null): boolean {
    return o1 && o2 ? this.getTransactionIdentifier(o1) === this.getTransactionIdentifier(o2) : o1 === o2;
  }

  addTransactionToCollectionIfMissing<Type extends Pick<ITransaction, 'id'>>(
    transactionCollection: Type[],
    ...transactionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const transactions: Type[] = transactionsToCheck.filter(transactionItem => transactionItem !== null && transactionItem !== undefined);
    if (transactions.length > 0) {
      const transactionCollectionIdentifiers = transactionCollection.map(transactionItem => this.getTransactionIdentifier(transactionItem));
      const transactionsToAdd = transactions.filter(transactionItem => {
        const transactionIdentifier = this.getTransactionIdentifier(transactionItem);
        if (transactionCollectionIdentifiers.includes(transactionIdentifier)) {
          return false;
        }
        transactionCollectionIdentifiers.push(transactionIdentifier);
        return true;
      });
      return [...transactionsToAdd, ...transactionCollection];
    }
    return transactionCollection;
  }

  protected convertValueFromClient<T extends ITransaction | NewTransaction | PartialUpdateTransaction>(transaction: T): RestOf<T> {
    return {
      ...transaction,
      createdAt: transaction.createdAt?.toJSON() ?? null,
      completedAt: transaction.completedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTransaction): ITransaction {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTransaction[]): ITransaction[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
