import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ILedgerAccount } from '../ledger-account.model';

@Component({
  selector: 'jhi-ledger-account-detail',
  templateUrl: './ledger-account-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class LedgerAccountDetail {
  readonly ledgerAccount = input<ILedgerAccount | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
