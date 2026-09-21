import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IAuditLogEntry } from '../audit-log-entry.model';

@Component({
  selector: 'jhi-audit-log-entry-detail',
  templateUrl: './audit-log-entry-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class AuditLogEntryDetail {
  readonly auditLogEntry = input<IAuditLogEntry | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
