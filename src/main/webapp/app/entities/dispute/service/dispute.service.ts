import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IDispute, NewDispute } from '../dispute.model';

export type PartialUpdateDispute = Partial<IDispute> & Pick<IDispute, 'id'>;

type RestOf<T extends IDispute | NewDispute> = Omit<T, 'dueDate' | 'evidenceSubmittedAt' | 'resolvedAt'> & {
  dueDate?: string | null;
  evidenceSubmittedAt?: string | null;
  resolvedAt?: string | null;
};

export type RestDispute = RestOf<IDispute>;

export type NewRestDispute = RestOf<NewDispute>;

export type PartialUpdateRestDispute = RestOf<PartialUpdateDispute>;

@Service()
export class DisputesService {
  readonly disputesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly disputesResource = httpResource<RestDispute[]>(() => {
    const params = this.disputesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of dispute that have been fetched. It is updated when the disputesResource emits a new value.
   * In case of error while fetching the disputes, the signal is set to an empty array.
   */
  readonly disputes = computed(() =>
    (this.disputesResource.hasValue() ? this.disputesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/disputes`;

  protected convertValueFromServer(restDispute: RestDispute): IDispute {
    return {
      ...restDispute,
      dueDate: restDispute.dueDate ? dayjs(restDispute.dueDate) : undefined,
      evidenceSubmittedAt: restDispute.evidenceSubmittedAt ? dayjs(restDispute.evidenceSubmittedAt) : undefined,
      resolvedAt: restDispute.resolvedAt ? dayjs(restDispute.resolvedAt) : undefined,
    };
  }
}

@Service()
export class DisputeService extends DisputesService {
  protected readonly http = inject(HttpClient);

  create(dispute: NewDispute): Observable<IDispute> {
    const copy = this.convertValueFromClient(dispute);
    return this.http.post<RestDispute>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dispute: IDispute): Observable<IDispute> {
    const copy = this.convertValueFromClient(dispute);
    return this.http
      .put<RestDispute>(`${this.resourceUrl}/${encodeURIComponent(this.getDisputeIdentifier(dispute))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dispute: PartialUpdateDispute): Observable<IDispute> {
    const copy = this.convertValueFromClient(dispute);
    return this.http
      .patch<RestDispute>(`${this.resourceUrl}/${encodeURIComponent(this.getDisputeIdentifier(dispute))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDispute> {
    return this.http
      .get<RestDispute>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDispute[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDispute[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDisputeIdentifier(dispute: Pick<IDispute, 'id'>): number {
    return dispute.id;
  }

  compareDispute(o1: Pick<IDispute, 'id'> | null, o2: Pick<IDispute, 'id'> | null): boolean {
    return o1 && o2 ? this.getDisputeIdentifier(o1) === this.getDisputeIdentifier(o2) : o1 === o2;
  }

  addDisputeToCollectionIfMissing<Type extends Pick<IDispute, 'id'>>(
    disputeCollection: Type[],
    ...disputesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const disputes: Type[] = disputesToCheck.filter(disputeItem => disputeItem !== null && disputeItem !== undefined);
    if (disputes.length > 0) {
      const disputeCollectionIdentifiers = disputeCollection.map(disputeItem => this.getDisputeIdentifier(disputeItem));
      const disputesToAdd = disputes.filter(disputeItem => {
        const disputeIdentifier = this.getDisputeIdentifier(disputeItem);
        if (disputeCollectionIdentifiers.includes(disputeIdentifier)) {
          return false;
        }
        disputeCollectionIdentifiers.push(disputeIdentifier);
        return true;
      });
      return [...disputesToAdd, ...disputeCollection];
    }
    return disputeCollection;
  }

  protected convertValueFromClient<T extends IDispute | NewDispute | PartialUpdateDispute>(dispute: T): RestOf<T> {
    return {
      ...dispute,
      dueDate: dispute.dueDate?.toJSON() ?? null,
      evidenceSubmittedAt: dispute.evidenceSubmittedAt?.toJSON() ?? null,
      resolvedAt: dispute.resolvedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDispute): IDispute {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDispute[]): IDispute[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
