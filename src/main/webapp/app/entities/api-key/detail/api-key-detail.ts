import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IApiKey } from '../api-key.model';

@Component({
  selector: 'jhi-api-key-detail',
  templateUrl: './api-key-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class ApiKeyDetail {
  readonly apiKey = input<IApiKey | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
