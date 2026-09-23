import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ITransaction } from '../transaction.model';

@Component({
  selector: 'jhi-transaction-detail',
  templateUrl: './transaction-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class TransactionDetail {
  readonly transaction = input<ITransaction | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
