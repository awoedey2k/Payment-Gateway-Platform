import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TenantDomainService } from '../service/tenant-domain.service';
import { ITenantDomain } from '../tenant-domain.model';

@Component({
  templateUrl: './tenant-domain-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TenantDomainDeleteDialog {
  tenantDomain?: ITenantDomain;

  protected readonly tenantDomainService = inject(TenantDomainService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantDomainService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
