import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICorporateTenant } from '../corporate-tenant.model';

@Component({
  selector: 'jhi-corporate-tenant-detail',
  templateUrl: './corporate-tenant-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class CorporateTenantDetail {
  readonly corporateTenant = input<ICorporateTenant | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
