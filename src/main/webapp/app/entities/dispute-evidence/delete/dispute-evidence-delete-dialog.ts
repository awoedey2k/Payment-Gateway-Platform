import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IDisputeEvidence } from '../dispute-evidence.model';
import { DisputeEvidenceService } from '../service/dispute-evidence.service';

@Component({
  templateUrl: './dispute-evidence-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class DisputeEvidenceDeleteDialog {
  disputeEvidence?: IDisputeEvidence;

  protected readonly disputeEvidenceService = inject(DisputeEvidenceService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.disputeEvidenceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
