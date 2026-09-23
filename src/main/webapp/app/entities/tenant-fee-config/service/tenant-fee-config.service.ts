import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ITenantFeeConfig, NewTenantFeeConfig } from '../tenant-fee-config.model';

export type PartialUpdateTenantFeeConfig = Partial<ITenantFeeConfig> & Pick<ITenantFeeConfig, 'id'>;

@Service()
export class TenantFeeConfigsService {
  readonly tenantFeeConfigsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly tenantFeeConfigsResource = httpResource<ITenantFeeConfig[]>(() => {
    const params = this.tenantFeeConfigsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of tenantFeeConfig that have been fetched. It is updated when the tenantFeeConfigsResource emits a new value.
   * In case of error while fetching the tenantFeeConfigs, the signal is set to an empty array.
   */
  readonly tenantFeeConfigs = computed(() => (this.tenantFeeConfigsResource.hasValue() ? this.tenantFeeConfigsResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/tenant-fee-configs`;
}

@Service()
export class TenantFeeConfigService extends TenantFeeConfigsService {
  protected readonly http = inject(HttpClient);

  create(tenantFeeConfig: NewTenantFeeConfig): Observable<ITenantFeeConfig> {
    return this.http.post<ITenantFeeConfig>(this.resourceUrl, tenantFeeConfig);
  }

  update(tenantFeeConfig: ITenantFeeConfig): Observable<ITenantFeeConfig> {
    return this.http.put<ITenantFeeConfig>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantFeeConfigIdentifier(tenantFeeConfig))}`,
      tenantFeeConfig,
    );
  }

  partialUpdate(tenantFeeConfig: PartialUpdateTenantFeeConfig): Observable<ITenantFeeConfig> {
    return this.http.patch<ITenantFeeConfig>(
      `${this.resourceUrl}/${encodeURIComponent(this.getTenantFeeConfigIdentifier(tenantFeeConfig))}`,
      tenantFeeConfig,
    );
  }

  find(id: number): Observable<ITenantFeeConfig> {
    return this.http.get<ITenantFeeConfig>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITenantFeeConfig[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITenantFeeConfig[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTenantFeeConfigIdentifier(tenantFeeConfig: Pick<ITenantFeeConfig, 'id'>): number {
    return tenantFeeConfig.id;
  }

  compareTenantFeeConfig(o1: Pick<ITenantFeeConfig, 'id'> | null, o2: Pick<ITenantFeeConfig, 'id'> | null): boolean {
    return o1 && o2 ? this.getTenantFeeConfigIdentifier(o1) === this.getTenantFeeConfigIdentifier(o2) : o1 === o2;
  }

  addTenantFeeConfigToCollectionIfMissing<Type extends Pick<ITenantFeeConfig, 'id'>>(
    tenantFeeConfigCollection: Type[],
    ...tenantFeeConfigsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tenantFeeConfigs: Type[] = tenantFeeConfigsToCheck.filter(
      tenantFeeConfigItem => tenantFeeConfigItem !== null && tenantFeeConfigItem !== undefined,
    );
    if (tenantFeeConfigs.length > 0) {
      const tenantFeeConfigCollectionIdentifiers = tenantFeeConfigCollection.map(tenantFeeConfigItem =>
        this.getTenantFeeConfigIdentifier(tenantFeeConfigItem),
      );
      const tenantFeeConfigsToAdd = tenantFeeConfigs.filter(tenantFeeConfigItem => {
        const tenantFeeConfigIdentifier = this.getTenantFeeConfigIdentifier(tenantFeeConfigItem);
        if (tenantFeeConfigCollectionIdentifiers.includes(tenantFeeConfigIdentifier)) {
          return false;
        }
        tenantFeeConfigCollectionIdentifiers.push(tenantFeeConfigIdentifier);
        return true;
      });
      return [...tenantFeeConfigsToAdd, ...tenantFeeConfigCollection];
    }
    return tenantFeeConfigCollection;
  }
}
