import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IDisputeEvidence, NewDisputeEvidence } from '../dispute-evidence.model';

export type PartialUpdateDisputeEvidence = Partial<IDisputeEvidence> & Pick<IDisputeEvidence, 'id'>;

type RestOf<T extends IDisputeEvidence | NewDisputeEvidence> = Omit<T, 'uploadedAt'> & {
  uploadedAt?: string | null;
};

export type RestDisputeEvidence = RestOf<IDisputeEvidence>;

export type NewRestDisputeEvidence = RestOf<NewDisputeEvidence>;

export type PartialUpdateRestDisputeEvidence = RestOf<PartialUpdateDisputeEvidence>;

@Service()
export class DisputeEvidencesService {
  readonly disputeEvidencesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly disputeEvidencesResource = httpResource<RestDisputeEvidence[]>(() => {
    const params = this.disputeEvidencesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of disputeEvidence that have been fetched. It is updated when the disputeEvidencesResource emits a new value.
   * In case of error while fetching the disputeEvidences, the signal is set to an empty array.
   */
  readonly disputeEvidences = computed(() =>
    (this.disputeEvidencesResource.hasValue() ? this.disputeEvidencesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/dispute-evidences`;

  protected convertValueFromServer(restDisputeEvidence: RestDisputeEvidence): IDisputeEvidence {
    return {
      ...restDisputeEvidence,
      uploadedAt: restDisputeEvidence.uploadedAt ? dayjs(restDisputeEvidence.uploadedAt) : undefined,
    };
  }
}

@Service()
export class DisputeEvidenceService extends DisputeEvidencesService {
  protected readonly http = inject(HttpClient);

  create(disputeEvidence: NewDisputeEvidence): Observable<IDisputeEvidence> {
    const copy = this.convertValueFromClient(disputeEvidence);
    return this.http.post<RestDisputeEvidence>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(disputeEvidence: IDisputeEvidence): Observable<IDisputeEvidence> {
    const copy = this.convertValueFromClient(disputeEvidence);
    return this.http
      .put<RestDisputeEvidence>(`${this.resourceUrl}/${encodeURIComponent(this.getDisputeEvidenceIdentifier(disputeEvidence))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(disputeEvidence: PartialUpdateDisputeEvidence): Observable<IDisputeEvidence> {
    const copy = this.convertValueFromClient(disputeEvidence);
    return this.http
      .patch<RestDisputeEvidence>(`${this.resourceUrl}/${encodeURIComponent(this.getDisputeEvidenceIdentifier(disputeEvidence))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDisputeEvidence> {
    return this.http
      .get<RestDisputeEvidence>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDisputeEvidence[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDisputeEvidence[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDisputeEvidenceIdentifier(disputeEvidence: Pick<IDisputeEvidence, 'id'>): number {
    return disputeEvidence.id;
  }

  compareDisputeEvidence(o1: Pick<IDisputeEvidence, 'id'> | null, o2: Pick<IDisputeEvidence, 'id'> | null): boolean {
    return o1 && o2 ? this.getDisputeEvidenceIdentifier(o1) === this.getDisputeEvidenceIdentifier(o2) : o1 === o2;
  }

  addDisputeEvidenceToCollectionIfMissing<Type extends Pick<IDisputeEvidence, 'id'>>(
    disputeEvidenceCollection: Type[],
    ...disputeEvidencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const disputeEvidences: Type[] = disputeEvidencesToCheck.filter(
      disputeEvidenceItem => disputeEvidenceItem !== null && disputeEvidenceItem !== undefined,
    );
    if (disputeEvidences.length > 0) {
      const disputeEvidenceCollectionIdentifiers = disputeEvidenceCollection.map(disputeEvidenceItem =>
        this.getDisputeEvidenceIdentifier(disputeEvidenceItem),
      );
      const disputeEvidencesToAdd = disputeEvidences.filter(disputeEvidenceItem => {
        const disputeEvidenceIdentifier = this.getDisputeEvidenceIdentifier(disputeEvidenceItem);
        if (disputeEvidenceCollectionIdentifiers.includes(disputeEvidenceIdentifier)) {
          return false;
        }
        disputeEvidenceCollectionIdentifiers.push(disputeEvidenceIdentifier);
        return true;
      });
      return [...disputeEvidencesToAdd, ...disputeEvidenceCollection];
    }
    return disputeEvidenceCollection;
  }

  protected convertValueFromClient<T extends IDisputeEvidence | NewDisputeEvidence | PartialUpdateDisputeEvidence>(
    disputeEvidence: T,
  ): RestOf<T> {
    return {
      ...disputeEvidence,
      uploadedAt: disputeEvidence.uploadedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDisputeEvidence): IDisputeEvidence {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDisputeEvidence[]): IDisputeEvidence[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
