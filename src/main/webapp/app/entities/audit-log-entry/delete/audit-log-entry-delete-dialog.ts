import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IAuditLogEntry } from '../audit-log-entry.model';
import { AuditLogEntryService } from '../service/audit-log-entry.service';

@Component({
  templateUrl: './audit-log-entry-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class AuditLogEntryDeleteDialog {
  auditLogEntry?: IAuditLogEntry;

  protected readonly auditLogEntryService = inject(AuditLogEntryService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.auditLogEntryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
