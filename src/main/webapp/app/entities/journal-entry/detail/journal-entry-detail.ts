import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IJournalEntry } from '../journal-entry.model';

@Component({
  selector: 'jhi-journal-entry-detail',
  templateUrl: './journal-entry-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class JournalEntryDetail {
  readonly journalEntry = input<IJournalEntry | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
