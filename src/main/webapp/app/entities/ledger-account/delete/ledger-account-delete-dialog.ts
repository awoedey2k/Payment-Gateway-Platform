import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { ILedgerAccount } from '../ledger-account.model';
import { LedgerAccountService } from '../service/ledger-account.service';

@Component({
  templateUrl: './ledger-account-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class LedgerAccountDeleteDialog {
  ledgerAccount?: ILedgerAccount;

  protected readonly ledgerAccountService = inject(LedgerAccountService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ledgerAccountService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
