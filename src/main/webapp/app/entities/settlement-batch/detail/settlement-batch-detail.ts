import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISettlementBatch } from '../settlement-batch.model';

@Component({
  selector: 'jhi-settlement-batch-detail',
  templateUrl: './settlement-batch-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class SettlementBatchDetail {
  readonly settlementBatch = input<ISettlementBatch | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
