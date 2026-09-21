import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { ISettlementBatch, NewSettlementBatch } from '../settlement-batch.model';

export type PartialUpdateSettlementBatch = Partial<ISettlementBatch> & Pick<ISettlementBatch, 'id'>;

type RestOf<T extends ISettlementBatch | NewSettlementBatch> = Omit<T, 'scheduledAt' | 'completedAt'> & {
  scheduledAt?: string | null;
  completedAt?: string | null;
};

export type RestSettlementBatch = RestOf<ISettlementBatch>;

export type NewRestSettlementBatch = RestOf<NewSettlementBatch>;

export type PartialUpdateRestSettlementBatch = RestOf<PartialUpdateSettlementBatch>;

@Service()
export class SettlementBatchesService {
  readonly settlementBatchesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly settlementBatchesResource = httpResource<RestSettlementBatch[]>(() => {
    const params = this.settlementBatchesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of settlementBatch that have been fetched. It is updated when the settlementBatchesResource emits a new value.
   * In case of error while fetching the settlementBatches, the signal is set to an empty array.
   */
  readonly settlementBatches = computed(() =>
    (this.settlementBatchesResource.hasValue() ? this.settlementBatchesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/settlement-batches`;

  protected convertValueFromServer(restSettlementBatch: RestSettlementBatch): ISettlementBatch {
    return {
      ...restSettlementBatch,
      scheduledAt: restSettlementBatch.scheduledAt ? dayjs(restSettlementBatch.scheduledAt) : undefined,
      completedAt: restSettlementBatch.completedAt ? dayjs(restSettlementBatch.completedAt) : undefined,
    };
  }
}

@Service()
export class SettlementBatchService extends SettlementBatchesService {
  protected readonly http = inject(HttpClient);

  create(settlementBatch: NewSettlementBatch): Observable<ISettlementBatch> {
    const copy = this.convertValueFromClient(settlementBatch);
    return this.http.post<RestSettlementBatch>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(settlementBatch: ISettlementBatch): Observable<ISettlementBatch> {
    const copy = this.convertValueFromClient(settlementBatch);
    return this.http
      .put<RestSettlementBatch>(`${this.resourceUrl}/${encodeURIComponent(this.getSettlementBatchIdentifier(settlementBatch))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(settlementBatch: PartialUpdateSettlementBatch): Observable<ISettlementBatch> {
    const copy = this.convertValueFromClient(settlementBatch);
    return this.http
      .patch<RestSettlementBatch>(`${this.resourceUrl}/${encodeURIComponent(this.getSettlementBatchIdentifier(settlementBatch))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ISettlementBatch> {
    return this.http
      .get<RestSettlementBatch>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ISettlementBatch[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSettlementBatch[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSettlementBatchIdentifier(settlementBatch: Pick<ISettlementBatch, 'id'>): number {
    return settlementBatch.id;
  }

  compareSettlementBatch(o1: Pick<ISettlementBatch, 'id'> | null, o2: Pick<ISettlementBatch, 'id'> | null): boolean {
    return o1 && o2 ? this.getSettlementBatchIdentifier(o1) === this.getSettlementBatchIdentifier(o2) : o1 === o2;
  }

  addSettlementBatchToCollectionIfMissing<Type extends Pick<ISettlementBatch, 'id'>>(
    settlementBatchCollection: Type[],
    ...settlementBatchesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const settlementBatches: Type[] = settlementBatchesToCheck.filter(
      settlementBatchItem => settlementBatchItem !== null && settlementBatchItem !== undefined,
    );
    if (settlementBatches.length > 0) {
      const settlementBatchCollectionIdentifiers = settlementBatchCollection.map(settlementBatchItem =>
        this.getSettlementBatchIdentifier(settlementBatchItem),
      );
      const settlementBatchesToAdd = settlementBatches.filter(settlementBatchItem => {
        const settlementBatchIdentifier = this.getSettlementBatchIdentifier(settlementBatchItem);
        if (settlementBatchCollectionIdentifiers.includes(settlementBatchIdentifier)) {
          return false;
        }
        settlementBatchCollectionIdentifiers.push(settlementBatchIdentifier);
        return true;
      });
      return [...settlementBatchesToAdd, ...settlementBatchCollection];
    }
    return settlementBatchCollection;
  }

  protected convertValueFromClient<T extends ISettlementBatch | NewSettlementBatch | PartialUpdateSettlementBatch>(
    settlementBatch: T,
  ): RestOf<T> {
    return {
      ...settlementBatch,
      scheduledAt: settlementBatch.scheduledAt?.toJSON() ?? null,
      completedAt: settlementBatch.completedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestSettlementBatch): ISettlementBatch {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestSettlementBatch[]): ISettlementBatch[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
