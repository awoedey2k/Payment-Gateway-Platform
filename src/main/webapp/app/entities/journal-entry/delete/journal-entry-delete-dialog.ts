import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IJournalEntry } from '../journal-entry.model';
import { JournalEntryService } from '../service/journal-entry.service';

@Component({
  templateUrl: './journal-entry-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class JournalEntryDeleteDialog {
  journalEntry?: IJournalEntry;

  protected readonly journalEntryService = inject(JournalEntryService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.journalEntryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
