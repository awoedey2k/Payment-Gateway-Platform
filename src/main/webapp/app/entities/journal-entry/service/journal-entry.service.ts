import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IJournalEntry, NewJournalEntry } from '../journal-entry.model';

export type PartialUpdateJournalEntry = Partial<IJournalEntry> & Pick<IJournalEntry, 'id'>;

type RestOf<T extends IJournalEntry | NewJournalEntry> = Omit<T, 'postedAt'> & {
  postedAt?: string | null;
};

export type RestJournalEntry = RestOf<IJournalEntry>;

export type NewRestJournalEntry = RestOf<NewJournalEntry>;

export type PartialUpdateRestJournalEntry = RestOf<PartialUpdateJournalEntry>;

@Service()
export class JournalEntriesService {
  readonly journalEntriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly journalEntriesResource = httpResource<RestJournalEntry[]>(() => {
    const params = this.journalEntriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of journalEntry that have been fetched. It is updated when the journalEntriesResource emits a new value.
   * In case of error while fetching the journalEntries, the signal is set to an empty array.
   */
  readonly journalEntries = computed(() =>
    (this.journalEntriesResource.hasValue() ? this.journalEntriesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/journal-entries`;

  protected convertValueFromServer(restJournalEntry: RestJournalEntry): IJournalEntry {
    return {
      ...restJournalEntry,
      postedAt: restJournalEntry.postedAt ? dayjs(restJournalEntry.postedAt) : undefined,
    };
  }
}

@Service()
export class JournalEntryService extends JournalEntriesService {
  protected readonly http = inject(HttpClient);

  create(journalEntry: NewJournalEntry): Observable<IJournalEntry> {
    const copy = this.convertValueFromClient(journalEntry);
    return this.http.post<RestJournalEntry>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(journalEntry: IJournalEntry): Observable<IJournalEntry> {
    const copy = this.convertValueFromClient(journalEntry);
    return this.http
      .put<RestJournalEntry>(`${this.resourceUrl}/${encodeURIComponent(this.getJournalEntryIdentifier(journalEntry))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(journalEntry: PartialUpdateJournalEntry): Observable<IJournalEntry> {
    const copy = this.convertValueFromClient(journalEntry);
    return this.http
      .patch<RestJournalEntry>(`${this.resourceUrl}/${encodeURIComponent(this.getJournalEntryIdentifier(journalEntry))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IJournalEntry> {
    return this.http
      .get<RestJournalEntry>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IJournalEntry[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestJournalEntry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getJournalEntryIdentifier(journalEntry: Pick<IJournalEntry, 'id'>): number {
    return journalEntry.id;
  }

  compareJournalEntry(o1: Pick<IJournalEntry, 'id'> | null, o2: Pick<IJournalEntry, 'id'> | null): boolean {
    return o1 && o2 ? this.getJournalEntryIdentifier(o1) === this.getJournalEntryIdentifier(o2) : o1 === o2;
  }

  addJournalEntryToCollectionIfMissing<Type extends Pick<IJournalEntry, 'id'>>(
    journalEntryCollection: Type[],
    ...journalEntriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const journalEntries: Type[] = journalEntriesToCheck.filter(
      journalEntryItem => journalEntryItem !== null && journalEntryItem !== undefined,
    );
    if (journalEntries.length > 0) {
      const journalEntryCollectionIdentifiers = journalEntryCollection.map(journalEntryItem =>
        this.getJournalEntryIdentifier(journalEntryItem),
      );
      const journalEntriesToAdd = journalEntries.filter(journalEntryItem => {
        const journalEntryIdentifier = this.getJournalEntryIdentifier(journalEntryItem);
        if (journalEntryCollectionIdentifiers.includes(journalEntryIdentifier)) {
          return false;
        }
        journalEntryCollectionIdentifiers.push(journalEntryIdentifier);
        return true;
      });
      return [...journalEntriesToAdd, ...journalEntryCollection];
    }
    return journalEntryCollection;
  }

  protected convertValueFromClient<T extends IJournalEntry | NewJournalEntry | PartialUpdateJournalEntry>(journalEntry: T): RestOf<T> {
    return {
      ...journalEntry,
      postedAt: journalEntry.postedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestJournalEntry): IJournalEntry {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestJournalEntry[]): IJournalEntry[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
