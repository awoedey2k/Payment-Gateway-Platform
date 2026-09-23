import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDispute } from '../dispute.model';

@Component({
  selector: 'jhi-dispute-detail',
  templateUrl: './dispute-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class DisputeDetail {
  readonly dispute = input<IDispute | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
