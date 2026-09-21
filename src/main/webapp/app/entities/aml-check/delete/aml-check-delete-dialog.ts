import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IAmlCheck } from '../aml-check.model';
import { AmlCheckService } from '../service/aml-check.service';

@Component({
  templateUrl: './aml-check-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class AmlCheckDeleteDialog {
  amlCheck?: IAmlCheck;

  protected readonly amlCheckService = inject(AmlCheckService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.amlCheckService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
