import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IAuditLogEntry, NewAuditLogEntry } from '../audit-log-entry.model';

export type PartialUpdateAuditLogEntry = Partial<IAuditLogEntry> & Pick<IAuditLogEntry, 'id'>;

type RestOf<T extends IAuditLogEntry | NewAuditLogEntry> = Omit<T, 'recordedAt'> & {
  recordedAt?: string | null;
};

export type RestAuditLogEntry = RestOf<IAuditLogEntry>;

export type NewRestAuditLogEntry = RestOf<NewAuditLogEntry>;

export type PartialUpdateRestAuditLogEntry = RestOf<PartialUpdateAuditLogEntry>;

@Service()
export class AuditLogEntriesService {
  readonly auditLogEntriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly auditLogEntriesResource = httpResource<RestAuditLogEntry[]>(() => {
    const params = this.auditLogEntriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of auditLogEntry that have been fetched. It is updated when the auditLogEntriesResource emits a new value.
   * In case of error while fetching the auditLogEntries, the signal is set to an empty array.
   */
  readonly auditLogEntries = computed(() =>
    (this.auditLogEntriesResource.hasValue() ? this.auditLogEntriesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/audit-log-entries`;

  protected convertValueFromServer(restAuditLogEntry: RestAuditLogEntry): IAuditLogEntry {
    return {
      ...restAuditLogEntry,
      recordedAt: restAuditLogEntry.recordedAt ? dayjs(restAuditLogEntry.recordedAt) : undefined,
    };
  }
}

@Service()
export class AuditLogEntryService extends AuditLogEntriesService {
  protected readonly http = inject(HttpClient);

  create(auditLogEntry: NewAuditLogEntry): Observable<IAuditLogEntry> {
    const copy = this.convertValueFromClient(auditLogEntry);
    return this.http.post<RestAuditLogEntry>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(auditLogEntry: IAuditLogEntry): Observable<IAuditLogEntry> {
    const copy = this.convertValueFromClient(auditLogEntry);
    return this.http
      .put<RestAuditLogEntry>(`${this.resourceUrl}/${encodeURIComponent(this.getAuditLogEntryIdentifier(auditLogEntry))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(auditLogEntry: PartialUpdateAuditLogEntry): Observable<IAuditLogEntry> {
    const copy = this.convertValueFromClient(auditLogEntry);
    return this.http
      .patch<RestAuditLogEntry>(`${this.resourceUrl}/${encodeURIComponent(this.getAuditLogEntryIdentifier(auditLogEntry))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAuditLogEntry> {
    return this.http
      .get<RestAuditLogEntry>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAuditLogEntry[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAuditLogEntry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAuditLogEntryIdentifier(auditLogEntry: Pick<IAuditLogEntry, 'id'>): number {
    return auditLogEntry.id;
  }

  compareAuditLogEntry(o1: Pick<IAuditLogEntry, 'id'> | null, o2: Pick<IAuditLogEntry, 'id'> | null): boolean {
    return o1 && o2 ? this.getAuditLogEntryIdentifier(o1) === this.getAuditLogEntryIdentifier(o2) : o1 === o2;
  }

  addAuditLogEntryToCollectionIfMissing<Type extends Pick<IAuditLogEntry, 'id'>>(
    auditLogEntryCollection: Type[],
    ...auditLogEntriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const auditLogEntries: Type[] = auditLogEntriesToCheck.filter(
      auditLogEntryItem => auditLogEntryItem !== null && auditLogEntryItem !== undefined,
    );
    if (auditLogEntries.length > 0) {
      const auditLogEntryCollectionIdentifiers = auditLogEntryCollection.map(auditLogEntryItem =>
        this.getAuditLogEntryIdentifier(auditLogEntryItem),
      );
      const auditLogEntriesToAdd = auditLogEntries.filter(auditLogEntryItem => {
        const auditLogEntryIdentifier = this.getAuditLogEntryIdentifier(auditLogEntryItem);
        if (auditLogEntryCollectionIdentifiers.includes(auditLogEntryIdentifier)) {
          return false;
        }
        auditLogEntryCollectionIdentifiers.push(auditLogEntryIdentifier);
        return true;
      });
      return [...auditLogEntriesToAdd, ...auditLogEntryCollection];
    }
    return auditLogEntryCollection;
  }

  protected convertValueFromClient<T extends IAuditLogEntry | NewAuditLogEntry | PartialUpdateAuditLogEntry>(auditLogEntry: T): RestOf<T> {
    return {
      ...auditLogEntry,
      recordedAt: auditLogEntry.recordedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAuditLogEntry): IAuditLogEntry {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAuditLogEntry[]): IAuditLogEntry[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
