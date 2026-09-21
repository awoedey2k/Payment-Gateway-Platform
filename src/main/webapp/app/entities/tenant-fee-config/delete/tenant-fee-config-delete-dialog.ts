import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TenantFeeConfigService } from '../service/tenant-fee-config.service';
import { ITenantFeeConfig } from '../tenant-fee-config.model';

@Component({
  templateUrl: './tenant-fee-config-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TenantFeeConfigDeleteDialog {
  tenantFeeConfig?: ITenantFeeConfig;

  protected readonly tenantFeeConfigService = inject(TenantFeeConfigService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantFeeConfigService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
