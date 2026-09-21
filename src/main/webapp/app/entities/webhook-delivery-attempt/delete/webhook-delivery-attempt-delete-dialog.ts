import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { WebhookDeliveryAttemptService } from '../service/webhook-delivery-attempt.service';
import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

@Component({
  templateUrl: './webhook-delivery-attempt-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class WebhookDeliveryAttemptDeleteDialog {
  webhookDeliveryAttempt?: IWebhookDeliveryAttempt;

  protected readonly webhookDeliveryAttemptService = inject(WebhookDeliveryAttemptService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.webhookDeliveryAttemptService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
