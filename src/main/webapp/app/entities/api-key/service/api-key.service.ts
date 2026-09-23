import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IApiKey, NewApiKey } from '../api-key.model';

export type PartialUpdateApiKey = Partial<IApiKey> & Pick<IApiKey, 'id'>;

type RestOf<T extends IApiKey | NewApiKey> = Omit<T, 'issuedAt' | 'revokedAt' | 'graceExpiresAt'> & {
  issuedAt?: string | null;
  revokedAt?: string | null;
  graceExpiresAt?: string | null;
};

export type RestApiKey = RestOf<IApiKey>;

export type NewRestApiKey = RestOf<NewApiKey>;

export type PartialUpdateRestApiKey = RestOf<PartialUpdateApiKey>;

@Service()
export class ApiKeysService {
  readonly apiKeysParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly apiKeysResource = httpResource<RestApiKey[]>(() => {
    const params = this.apiKeysParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of apiKey that have been fetched. It is updated when the apiKeysResource emits a new value.
   * In case of error while fetching the apiKeys, the signal is set to an empty array.
   */
  readonly apiKeys = computed(() =>
    (this.apiKeysResource.hasValue() ? this.apiKeysResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/api-keys`;

  protected convertValueFromServer(restApiKey: RestApiKey): IApiKey {
    return {
      ...restApiKey,
      issuedAt: restApiKey.issuedAt ? dayjs(restApiKey.issuedAt) : undefined,
      revokedAt: restApiKey.revokedAt ? dayjs(restApiKey.revokedAt) : undefined,
      graceExpiresAt: restApiKey.graceExpiresAt ? dayjs(restApiKey.graceExpiresAt) : undefined,
    };
  }
}

@Service()
export class ApiKeyService extends ApiKeysService {
  protected readonly http = inject(HttpClient);

  create(apiKey: NewApiKey): Observable<IApiKey> {
    const copy = this.convertValueFromClient(apiKey);
    return this.http.post<RestApiKey>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(apiKey: IApiKey): Observable<IApiKey> {
    const copy = this.convertValueFromClient(apiKey);
    return this.http
      .put<RestApiKey>(`${this.resourceUrl}/${encodeURIComponent(this.getApiKeyIdentifier(apiKey))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(apiKey: PartialUpdateApiKey): Observable<IApiKey> {
    const copy = this.convertValueFromClient(apiKey);
    return this.http
      .patch<RestApiKey>(`${this.resourceUrl}/${encodeURIComponent(this.getApiKeyIdentifier(apiKey))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IApiKey> {
    return this.http.get<RestApiKey>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IApiKey[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestApiKey[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getApiKeyIdentifier(apiKey: Pick<IApiKey, 'id'>): number {
    return apiKey.id;
  }

  compareApiKey(o1: Pick<IApiKey, 'id'> | null, o2: Pick<IApiKey, 'id'> | null): boolean {
    return o1 && o2 ? this.getApiKeyIdentifier(o1) === this.getApiKeyIdentifier(o2) : o1 === o2;
  }

  addApiKeyToCollectionIfMissing<Type extends Pick<IApiKey, 'id'>>(
    apiKeyCollection: Type[],
    ...apiKeysToCheck: (Type | null | undefined)[]
  ): Type[] {
    const apiKeys: Type[] = apiKeysToCheck.filter(apiKeyItem => apiKeyItem !== null && apiKeyItem !== undefined);
    if (apiKeys.length > 0) {
      const apiKeyCollectionIdentifiers = apiKeyCollection.map(apiKeyItem => this.getApiKeyIdentifier(apiKeyItem));
      const apiKeysToAdd = apiKeys.filter(apiKeyItem => {
        const apiKeyIdentifier = this.getApiKeyIdentifier(apiKeyItem);
        if (apiKeyCollectionIdentifiers.includes(apiKeyIdentifier)) {
          return false;
        }
        apiKeyCollectionIdentifiers.push(apiKeyIdentifier);
        return true;
      });
      return [...apiKeysToAdd, ...apiKeyCollection];
    }
    return apiKeyCollection;
  }

  protected convertValueFromClient<T extends IApiKey | NewApiKey | PartialUpdateApiKey>(apiKey: T): RestOf<T> {
    return {
      ...apiKey,
      issuedAt: apiKey.issuedAt?.toJSON() ?? null,
      revokedAt: apiKey.revokedAt?.toJSON() ?? null,
      graceExpiresAt: apiKey.graceExpiresAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestApiKey): IApiKey {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestApiKey[]): IApiKey[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
