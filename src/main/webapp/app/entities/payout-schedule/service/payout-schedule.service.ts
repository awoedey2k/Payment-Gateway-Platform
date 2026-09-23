import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IPayoutSchedule, NewPayoutSchedule } from '../payout-schedule.model';

export type PartialUpdatePayoutSchedule = Partial<IPayoutSchedule> & Pick<IPayoutSchedule, 'id'>;

@Service()
export class PayoutSchedulesService {
  readonly payoutSchedulesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly payoutSchedulesResource = httpResource<IPayoutSchedule[]>(() => {
    const params = this.payoutSchedulesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of payoutSchedule that have been fetched. It is updated when the payoutSchedulesResource emits a new value.
   * In case of error while fetching the payoutSchedules, the signal is set to an empty array.
   */
  readonly payoutSchedules = computed(() => (this.payoutSchedulesResource.hasValue() ? this.payoutSchedulesResource.value() : []));
  protected readonly resourceUrl = `${serverApiUrl}api/payout-schedules`;
}

@Service()
export class PayoutScheduleService extends PayoutSchedulesService {
  protected readonly http = inject(HttpClient);

  create(payoutSchedule: NewPayoutSchedule): Observable<IPayoutSchedule> {
    return this.http.post<IPayoutSchedule>(this.resourceUrl, payoutSchedule);
  }

  update(payoutSchedule: IPayoutSchedule): Observable<IPayoutSchedule> {
    return this.http.put<IPayoutSchedule>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPayoutScheduleIdentifier(payoutSchedule))}`,
      payoutSchedule,
    );
  }

  partialUpdate(payoutSchedule: PartialUpdatePayoutSchedule): Observable<IPayoutSchedule> {
    return this.http.patch<IPayoutSchedule>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPayoutScheduleIdentifier(payoutSchedule))}`,
      payoutSchedule,
    );
  }

  find(id: number): Observable<IPayoutSchedule> {
    return this.http.get<IPayoutSchedule>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPayoutSchedule[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPayoutSchedule[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPayoutScheduleIdentifier(payoutSchedule: Pick<IPayoutSchedule, 'id'>): number {
    return payoutSchedule.id;
  }

  comparePayoutSchedule(o1: Pick<IPayoutSchedule, 'id'> | null, o2: Pick<IPayoutSchedule, 'id'> | null): boolean {
    return o1 && o2 ? this.getPayoutScheduleIdentifier(o1) === this.getPayoutScheduleIdentifier(o2) : o1 === o2;
  }

  addPayoutScheduleToCollectionIfMissing<Type extends Pick<IPayoutSchedule, 'id'>>(
    payoutScheduleCollection: Type[],
    ...payoutSchedulesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const payoutSchedules: Type[] = payoutSchedulesToCheck.filter(
      payoutScheduleItem => payoutScheduleItem !== null && payoutScheduleItem !== undefined,
    );
    if (payoutSchedules.length > 0) {
      const payoutScheduleCollectionIdentifiers = payoutScheduleCollection.map(payoutScheduleItem =>
        this.getPayoutScheduleIdentifier(payoutScheduleItem),
      );
      const payoutSchedulesToAdd = payoutSchedules.filter(payoutScheduleItem => {
        const payoutScheduleIdentifier = this.getPayoutScheduleIdentifier(payoutScheduleItem);
        if (payoutScheduleCollectionIdentifiers.includes(payoutScheduleIdentifier)) {
          return false;
        }
        payoutScheduleCollectionIdentifiers.push(payoutScheduleIdentifier);
        return true;
      });
      return [...payoutSchedulesToAdd, ...payoutScheduleCollection];
    }
    return payoutScheduleCollection;
  }
}
