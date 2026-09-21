import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IJournalLine } from '../journal-line.model';
import { JournalLineService } from '../service/journal-line.service';

@Component({
  templateUrl: './journal-line-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class JournalLineDeleteDialog {
  journalLine?: IJournalLine;

  protected readonly journalLineService = inject(JournalLineService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.journalLineService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
