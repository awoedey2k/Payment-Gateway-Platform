import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IForexRate } from '../forex-rate.model';
import { ForexRateService } from '../service/forex-rate.service';

@Component({
  templateUrl: './forex-rate-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ForexRateDeleteDialog {
  forexRate?: IForexRate;

  protected readonly forexRateService = inject(ForexRateService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.forexRateService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
