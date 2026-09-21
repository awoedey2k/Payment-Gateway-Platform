import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IAmlCheck } from '../aml-check.model';

@Component({
  selector: 'jhi-aml-check-detail',
  templateUrl: './aml-check-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class AmlCheckDetail {
  readonly amlCheck = input<IAmlCheck | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
