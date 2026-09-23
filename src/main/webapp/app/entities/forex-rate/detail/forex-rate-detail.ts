import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IForexRate } from '../forex-rate.model';

@Component({
  selector: 'jhi-forex-rate-detail',
  templateUrl: './forex-rate-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class ForexRateDetail {
  readonly forexRate = input<IForexRate | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
