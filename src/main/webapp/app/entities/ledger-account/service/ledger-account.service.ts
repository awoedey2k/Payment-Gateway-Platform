import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ILedgerAccount, NewLedgerAccount } from '../ledger-account.model';

export type PartialUpdateLedgerAccount = Partial<ILedgerAccount> & Pick<ILedgerAccount, 'id'>;

@Service()
export class LedgerAccountsService {
  readonly ledgerAccountsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ledgerAccountsResource = httpResource<ILedgerAccount[]>(() => {
    const params = this.ledgerAccountsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ledgerAccount that have been fetched. It is updated when the ledgerAccountsResource emits a new value.
   * In case of error while fetching the ledgerAccounts, the signal is set to an empty array.
   */
  readonly ledgerAccounts = computed(() => (this.ledgerAccountsResource.hasValue() ? this.ledgerAccountsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/ledger-accounts`;
}

@Service()
export class LedgerAccountService extends LedgerAccountsService {
  protected readonly http = inject(HttpClient);

  create(ledgerAccount: NewLedgerAccount): Observable<ILedgerAccount> {
    return this.http.post<ILedgerAccount>(this.resourceUrl, ledgerAccount);
  }

  update(ledgerAccount: ILedgerAccount): Observable<ILedgerAccount> {
    return this.http.put<ILedgerAccount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLedgerAccountIdentifier(ledgerAccount))}`,
      ledgerAccount,
    );
  }

  partialUpdate(ledgerAccount: PartialUpdateLedgerAccount): Observable<ILedgerAccount> {
    return this.http.patch<ILedgerAccount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLedgerAccountIdentifier(ledgerAccount))}`,
      ledgerAccount,
    );
  }

  find(id: number): Observable<ILedgerAccount> {
    return this.http.get<ILedgerAccount>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILedgerAccount[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILedgerAccount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLedgerAccountIdentifier(ledgerAccount: Pick<ILedgerAccount, 'id'>): number {
    return ledgerAccount.id;
  }

  compareLedgerAccount(o1: Pick<ILedgerAccount, 'id'> | null, o2: Pick<ILedgerAccount, 'id'> | null): boolean {
    return o1 && o2 ? this.getLedgerAccountIdentifier(o1) === this.getLedgerAccountIdentifier(o2) : o1 === o2;
  }

  addLedgerAccountToCollectionIfMissing<Type extends Pick<ILedgerAccount, 'id'>>(
    ledgerAccountCollection: Type[],
    ...ledgerAccountsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ledgerAccounts: Type[] = ledgerAccountsToCheck.filter(
      ledgerAccountItem => ledgerAccountItem !== null && ledgerAccountItem !== undefined,
    );
    if (ledgerAccounts.length > 0) {
      const ledgerAccountCollectionIdentifiers = ledgerAccountCollection.map(ledgerAccountItem =>
        this.getLedgerAccountIdentifier(ledgerAccountItem),
      );
      const ledgerAccountsToAdd = ledgerAccounts.filter(ledgerAccountItem => {
        const ledgerAccountIdentifier = this.getLedgerAccountIdentifier(ledgerAccountItem);
        if (ledgerAccountCollectionIdentifiers.includes(ledgerAccountIdentifier)) {
          return false;
        }
        ledgerAccountCollectionIdentifiers.push(ledgerAccountIdentifier);
        return true;
      });
      return [...ledgerAccountsToAdd, ...ledgerAccountCollection];
    }
    return ledgerAccountCollection;
  }
}
