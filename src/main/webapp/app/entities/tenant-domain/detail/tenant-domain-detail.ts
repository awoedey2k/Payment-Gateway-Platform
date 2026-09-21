import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ITenantDomain } from '../tenant-domain.model';

@Component({
  selector: 'jhi-tenant-domain-detail',
  templateUrl: './tenant-domain-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class TenantDomainDetail {
  readonly tenantDomain = input<ITenantDomain | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
