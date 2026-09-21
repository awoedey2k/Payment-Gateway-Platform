import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TenantWalletService } from '../service/tenant-wallet.service';
import { ITenantWallet } from '../tenant-wallet.model';

@Component({
  templateUrl: './tenant-wallet-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TenantWalletDeleteDialog {
  tenantWallet?: ITenantWallet;

  protected readonly tenantWalletService = inject(TenantWalletService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantWalletService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
