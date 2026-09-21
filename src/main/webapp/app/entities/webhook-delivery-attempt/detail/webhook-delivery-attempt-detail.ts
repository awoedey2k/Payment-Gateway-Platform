import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

@Component({
  selector: 'jhi-webhook-delivery-attempt-detail',
  templateUrl: './webhook-delivery-attempt-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class WebhookDeliveryAttemptDetail {
  readonly webhookDeliveryAttempt = input<IWebhookDeliveryAttempt | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
