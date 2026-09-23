import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { SettlementBatchService } from '../service/settlement-batch.service';
import { ISettlementBatch } from '../settlement-batch.model';

@Component({
  templateUrl: './settlement-batch-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class SettlementBatchDeleteDialog {
  settlementBatch?: ISettlementBatch;

  protected readonly settlementBatchService = inject(SettlementBatchService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.settlementBatchService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
