import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IJournalLine } from '../journal-line.model';

@Component({
  selector: 'jhi-journal-line-detail',
  templateUrl: './journal-line-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class JournalLineDetail {
  readonly journalLine = input<IJournalLine | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
