import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IWebhookSubscription } from '../webhook-subscription.model';

@Component({
  selector: 'jhi-webhook-subscription-detail',
  templateUrl: './webhook-subscription-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class WebhookSubscriptionDetail {
  readonly webhookSubscription = input<IWebhookSubscription | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
