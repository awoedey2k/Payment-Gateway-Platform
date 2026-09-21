import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';

@Component({
  templateUrl: './transaction-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TransactionDeleteDialog {
  transaction?: ITransaction;

  protected readonly transactionService = inject(TransactionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.transactionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
