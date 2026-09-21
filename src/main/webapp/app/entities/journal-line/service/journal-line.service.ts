import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IJournalLine, NewJournalLine } from '../journal-line.model';

export type PartialUpdateJournalLine = Partial<IJournalLine> & Pick<IJournalLine, 'id'>;

@Service()
export class JournalLinesService {
  readonly journalLinesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly journalLinesResource = httpResource<IJournalLine[]>(() => {
    const params = this.journalLinesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of journalLine that have been fetched. It is updated when the journalLinesResource emits a new value.
   * In case of error while fetching the journalLines, the signal is set to an empty array.
   */
  readonly journalLines = computed(() => (this.journalLinesResource.hasValue() ? this.journalLinesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/journal-lines`;
}

@Service()
export class JournalLineService extends JournalLinesService {
  protected readonly http = inject(HttpClient);

  create(journalLine: NewJournalLine): Observable<IJournalLine> {
    return this.http.post<IJournalLine>(this.resourceUrl, journalLine);
  }

  update(journalLine: IJournalLine): Observable<IJournalLine> {
    return this.http.put<IJournalLine>(
      `${this.resourceUrl}/${encodeURIComponent(this.getJournalLineIdentifier(journalLine))}`,
      journalLine,
    );
  }

  partialUpdate(journalLine: PartialUpdateJournalLine): Observable<IJournalLine> {
    return this.http.patch<IJournalLine>(
      `${this.resourceUrl}/${encodeURIComponent(this.getJournalLineIdentifier(journalLine))}`,
      journalLine,
    );
  }

  find(id: number): Observable<IJournalLine> {
    return this.http.get<IJournalLine>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IJournalLine[]>> {
    const options = createRequestOption(req);
    return this.http.get<IJournalLine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getJournalLineIdentifier(journalLine: Pick<IJournalLine, 'id'>): number {
    return journalLine.id;
  }

  compareJournalLine(o1: Pick<IJournalLine, 'id'> | null, o2: Pick<IJournalLine, 'id'> | null): boolean {
    return o1 && o2 ? this.getJournalLineIdentifier(o1) === this.getJournalLineIdentifier(o2) : o1 === o2;
  }

  addJournalLineToCollectionIfMissing<Type extends Pick<IJournalLine, 'id'>>(
    journalLineCollection: Type[],
    ...journalLinesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const journalLines: Type[] = journalLinesToCheck.filter(journalLineItem => journalLineItem !== null && journalLineItem !== undefined);
    if (journalLines.length > 0) {
      const journalLineCollectionIdentifiers = journalLineCollection.map(journalLineItem => this.getJournalLineIdentifier(journalLineItem));
      const journalLinesToAdd = journalLines.filter(journalLineItem => {
        const journalLineIdentifier = this.getJournalLineIdentifier(journalLineItem);
        if (journalLineCollectionIdentifiers.includes(journalLineIdentifier)) {
          return false;
        }
        journalLineCollectionIdentifiers.push(journalLineIdentifier);
        return true;
      });
      return [...journalLinesToAdd, ...journalLineCollection];
    }
    return journalLineCollection;
  }
}
