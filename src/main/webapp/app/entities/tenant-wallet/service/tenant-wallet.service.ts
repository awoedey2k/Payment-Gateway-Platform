import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITenantWallet, NewTenantWallet } from '../tenant-wallet.model';

export type PartialUpdateTenantWallet = Partial<ITenantWallet> & Pick<ITenantWallet, 'id'>;

@Service()
export class TenantWalletsService {
  readonly tenantWalletsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tenantWalletsResource = httpResource<ITenantWallet[]>(() => {
    const params = this.tenantWalletsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tenantWallet that have been fetched. It is updated when the tenantWalletsResource emits a new value.
   * In case of error while fetching the tenantWallets, the signal is set to an empty array.
   */
  readonly tenantWallets = computed(() => (this.tenantWalletsResource.hasValue() ? this.tenantWalletsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/tenant-wallets`;
}

@Service()
export class TenantWalletService extends TenantWalletsService {
  protected readonly http = inject(HttpClient);

  create(tenantWallet: NewTenantWallet): Observable<ITenantWallet> {
    return this.http.post<ITenantWallet>(this.resourceUrl, tenantWallet);
  }

  update(tenantWallet: ITenantWallet): Observable<ITenantWallet> {
    return this.http.put<ITenantWallet>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantWalletIdentifier(tenantWallet))}`,
      tenantWallet,
    );
  }

  partialUpdate(tenantWallet: PartialUpdateTenantWallet): Observable<ITenantWallet> {
    return this.http.patch<ITenantWallet>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantWalletIdentifier(tenantWallet))}`,
      tenantWallet,
    );
  }

  find(id: number): Observable<ITenantWallet> {
    return this.http.get<ITenantWallet>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITenantWallet[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITenantWallet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTenantWalletIdentifier(tenantWallet: Pick<ITenantWallet, 'id'>): number {
    return tenantWallet.id;
  }

  compareTenantWallet(o1: Pick<ITenantWallet, 'id'> | null, o2: Pick<ITenantWallet, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantWalletIdentifier(o1) === this.getTenantWalletIdentifier(o2) : o1 === o2;
  }

  addTenantWalletToCollectionIfMissing<Type extends Pick<ITenantWallet, 'id'>>(
    tenantWalletCollection: Type[],
    ...tenantWalletsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenantWallets: Type[] = tenantWalletsToCheck.filter(
      tenantWalletItem => tenantWalletItem !== null && tenantWalletItem !== undefined,
    );
    if (tenantWallets.length > 0) {
      const tenantWalletCollectionIdentifiers = tenantWalletCollection.map(tenantWalletItem =>
        this.getTenantWalletIdentifier(tenantWalletItem),
      );
      const tenantWalletsToAdd = tenantWallets.filter(tenantWalletItem => {
        const tenantWalletIdentifier = this.getTenantWalletIdentifier(tenantWalletItem);
        if (tenantWalletCollectionIdentifiers.includes(tenantWalletIdentifier)) {
          return false;
        }
        tenantWalletCollectionIdentifiers.push(tenantWalletIdentifier);
        return true;
      });
      return [...tenantWalletsToAdd, ...tenantWalletCollection];
    }
    return tenantWalletCollection;
  }
}
