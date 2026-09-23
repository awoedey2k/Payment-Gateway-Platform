import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IWebhookSubscription, NewWebhookSubscription } from '../webhook-subscription.model';

export type PartialUpdateWebhookSubscription = Partial<IWebhookSubscription> & Pick<IWebhookSubscription, 'id'>;

@Service()
export class WebhookSubscriptionsService {
  readonly webhookSubscriptionsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly webhookSubscriptionsResource = httpResource<IWebhookSubscription[]>(() => {
    const params = this.webhookSubscriptionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of webhookSubscription that have been fetched. It is updated when the webhookSubscriptionsResource emits a new value.
   * In case of error while fetching the webhookSubscriptions, the signal is set to an empty array.
   */
  readonly webhookSubscriptions = computed(() =>
    this.webhookSubscriptionsResource.hasValue() ? this.webhookSubscriptionsResource.value() : [],
  );
  protected readonly resourceUrl = `${serverApiUrl}api/webhook-subscriptions`;
}

@Service()
export class WebhookSubscriptionService extends WebhookSubscriptionsService {
  protected readonly http = inject(HttpClient);

  create(webhookSubscription: NewWebhookSubscription): Observable<IWebhookSubscription> {
    return this.http.post<IWebhookSubscription>(this.resourceUrl, webhookSubscription);
  }

  update(webhookSubscription: IWebhookSubscription): Observable<IWebhookSubscription> {
    return this.http.put<IWebhookSubscription>(
      `${this.resourceUrl}/${encodeURIComponent(this.getWebhookSubscriptionIdentifier(webhookSubscription))}`,
      webhookSubscription,
    );
  }

  partialUpdate(webhookSubscription: PartialUpdateWebhookSubscription): Observable<IWebhookSubscription> {
    return this.http.patch<IWebhookSubscription>(
      `${this.resourceUrl}/${encodeURIComponent(this.getWebhookSubscriptionIdentifier(webhookSubscription))}`,
      webhookSubscription,
    );
  }

  find(id: number): Observable<IWebhookSubscription> {
    return this.http.get<IWebhookSubscription>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IWebhookSubscription[]>> {
    const options = createRequestOption(req);
    return this.http.get<IWebhookSubscription[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getWebhookSubscriptionIdentifier(webhookSubscription: Pick<IWebhookSubscription, 'id'>): number {
    return webhookSubscription.id;
  }

  compareWebhookSubscription(o1: Pick<IWebhookSubscription, 'id'> | null, o2: Pick<IWebhookSubscription, 'id'> | null): boolean {
    return o1 && o2 ? this.getWebhookSubscriptionIdentifier(o1) === this.getWebhookSubscriptionIdentifier(o2) : o1 === o2;
  }

  addWebhookSubscriptionToCollectionIfMissing<Type extends Pick<IWebhookSubscription, 'id'>>(
    webhookSubscriptionCollection: Type[],
    ...webhookSubscriptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const webhookSubscriptions: Type[] = webhookSubscriptionsToCheck.filter(
      webhookSubscriptionItem => webhookSubscriptionItem !== null && webhookSubscriptionItem !== undefined,
    );
    if (webhookSubscriptions.length > 0) {
      const webhookSubscriptionCollectionIdentifiers = webhookSubscriptionCollection.map(webhookSubscriptionItem =>
        this.getWebhookSubscriptionIdentifier(webhookSubscriptionItem),
      );
      const webhookSubscriptionsToAdd = webhookSubscriptions.filter(webhookSubscriptionItem => {
        const webhookSubscriptionIdentifier = this.getWebhookSubscriptionIdentifier(webhookSubscriptionItem);
        if (webhookSubscriptionCollectionIdentifiers.includes(webhookSubscriptionIdentifier)) {
          return false;
        }
        webhookSubscriptionCollectionIdentifiers.push(webhookSubscriptionIdentifier);
        return true;
      });
      return [...webhookSubscriptionsToAdd, ...webhookSubscriptionCollection];
    }
    return webhookSubscriptionCollection;
  }
}
