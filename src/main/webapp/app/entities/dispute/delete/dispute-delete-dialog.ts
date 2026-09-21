import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IDispute } from '../dispute.model';
import { DisputeService } from '../service/dispute.service';

@Component({
  templateUrl: './dispute-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class DisputeDeleteDialog {
  dispute?: IDispute;

  protected readonly disputeService = inject(DisputeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.disputeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
