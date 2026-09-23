import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ITenantDirector } from '../tenant-director.model';

@Component({
  selector: 'jhi-tenant-director-detail',
  templateUrl: './tenant-director-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatePipe],
})
export class TenantDirectorDetail {
  readonly tenantDirector = input<ITenantDirector | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
