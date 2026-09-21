import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IWebhookDeliveryAttempt, NewWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

export type PartialUpdateWebhookDeliveryAttempt = Partial<IWebhookDeliveryAttempt> & Pick<IWebhookDeliveryAttempt, 'id'>;

type RestOf<T extends IWebhookDeliveryAttempt | NewWebhookDeliveryAttempt> = Omit<T, 'attemptedAt'> & {
  attemptedAt?: string | null;
};

export type RestWebhookDeliveryAttempt = RestOf<IWebhookDeliveryAttempt>;

export type NewRestWebhookDeliveryAttempt = RestOf<NewWebhookDeliveryAttempt>;

export type PartialUpdateRestWebhookDeliveryAttempt = RestOf<PartialUpdateWebhookDeliveryAttempt>;

@Service()
export class WebhookDeliveryAttemptsService {
  readonly webhookDeliveryAttemptsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly webhookDeliveryAttemptsResource = httpResource<RestWebhookDeliveryAttempt[]>(() => {
    const params = this.webhookDeliveryAttemptsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of webhookDeliveryAttempt that have been fetched. It is updated when the webhookDeliveryAttemptsResource emits a new value.
   * In case of error while fetching the webhookDeliveryAttempts, the signal is set to an empty array.
   */
  readonly webhookDeliveryAttempts = computed(() =>
    (this.webhookDeliveryAttemptsResource.hasValue() ? this.webhookDeliveryAttemptsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/webhook-delivery-attempts`;

  protected convertValueFromServer(restWebhookDeliveryAttempt: RestWebhookDeliveryAttempt): IWebhookDeliveryAttempt {
    return {
      ...restWebhookDeliveryAttempt,
      attemptedAt: restWebhookDeliveryAttempt.attemptedAt ? dayjs(restWebhookDeliveryAttempt.attemptedAt) : undefined,
    };
  }
}

@Service()
export class WebhookDeliveryAttemptService extends WebhookDeliveryAttemptsService {
  protected readonly http = inject(HttpClient);

  create(webhookDeliveryAttempt: NewWebhookDeliveryAttempt): Observable<IWebhookDeliveryAttempt> {
    const copy = this.convertValueFromClient(webhookDeliveryAttempt);
    return this.http.post<RestWebhookDeliveryAttempt>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(webhookDeliveryAttempt: IWebhookDeliveryAttempt): Observable<IWebhookDeliveryAttempt> {
    const copy = this.convertValueFromClient(webhookDeliveryAttempt);
    return this.http
      .put<RestWebhookDeliveryAttempt>(
        `${this.resourceUrl}/${encodeURIComponent(this.getWebhookDeliveryAttemptIdentifier(webhookDeliveryAttempt))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(webhookDeliveryAttempt: PartialUpdateWebhookDeliveryAttempt): Observable<IWebhookDeliveryAttempt> {
    const copy = this.convertValueFromClient(webhookDeliveryAttempt);
    return this.http
      .patch<RestWebhookDeliveryAttempt>(
        `${this.resourceUrl}/${encodeURIComponent(this.getWebhookDeliveryAttemptIdentifier(webhookDeliveryAttempt))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IWebhookDeliveryAttempt> {
    return this.http
      .get<RestWebhookDeliveryAttempt>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IWebhookDeliveryAttempt[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWebhookDeliveryAttempt[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getWebhookDeliveryAttemptIdentifier(webhookDeliveryAttempt: Pick<IWebhookDeliveryAttempt, 'id'>): number {
    return webhookDeliveryAttempt.id;
  }

  compareWebhookDeliveryAttempt(o1: Pick<IWebhookDeliveryAttempt, 'id'> | null, o2: Pick<IWebhookDeliveryAttempt, 'id'> | null): boolean {
    return o1 && o2 ? this.getWebhookDeliveryAttemptIdentifier(o1) === this.getWebhookDeliveryAttemptIdentifier(o2) : o1 === o2;
  }

  addWebhookDeliveryAttemptToCollectionIfMissing<Type extends Pick<IWebhookDeliveryAttempt, 'id'>>(
    webhookDeliveryAttemptCollection: Type[],
    ...webhookDeliveryAttemptsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const webhookDeliveryAttempts: Type[] = webhookDeliveryAttemptsToCheck.filter(
      webhookDeliveryAttemptItem => webhookDeliveryAttemptItem !== null && webhookDeliveryAttemptItem !== undefined,
    );
    if (webhookDeliveryAttempts.length > 0) {
      const webhookDeliveryAttemptCollectionIdentifiers = webhookDeliveryAttemptCollection.map(webhookDeliveryAttemptItem =>
        this.getWebhookDeliveryAttemptIdentifier(webhookDeliveryAttemptItem),
      );
      const webhookDeliveryAttemptsToAdd = webhookDeliveryAttempts.filter(webhookDeliveryAttemptItem => {
        const webhookDeliveryAttemptIdentifier = this.getWebhookDeliveryAttemptIdentifier(webhookDeliveryAttemptItem);
        if (webhookDeliveryAttemptCollectionIdentifiers.includes(webhookDeliveryAttemptIdentifier)) {
          return false;
        }
        webhookDeliveryAttemptCollectionIdentifiers.push(webhookDeliveryAttemptIdentifier);
        return true;
      });
      return [...webhookDeliveryAttemptsToAdd, ...webhookDeliveryAttemptCollection];
    }
    return webhookDeliveryAttemptCollection;
  }

  protected convertValueFromClient<T extends IWebhookDeliveryAttempt | NewWebhookDeliveryAttempt | PartialUpdateWebhookDeliveryAttempt>(
    webhookDeliveryAttempt: T,
  ): RestOf<T> {
    return {
      ...webhookDeliveryAttempt,
      attemptedAt: webhookDeliveryAttempt.attemptedAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestWebhookDeliveryAttempt): IWebhookDeliveryAttempt {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestWebhookDeliveryAttempt[]): IWebhookDeliveryAttempt[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
