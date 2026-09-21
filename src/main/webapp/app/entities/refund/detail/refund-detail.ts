import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IRefund } from '../refund.model';

@Component({
  selector: 'jhi-refund-detail',
  templateUrl: './refund-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class RefundDetail {
  readonly refund = input<IRefund | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
