import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { TenantDirectorService } from '../service/tenant-director.service';
import { ITenantDirector } from '../tenant-director.model';

@Component({
  templateUrl: './tenant-director-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class TenantDirectorDeleteDialog {
  tenantDirector?: ITenantDirector;

  protected readonly tenantDirectorService = inject(TenantDirectorService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tenantDirectorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
