import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ITenantFeeConfig } from '../tenant-fee-config.model';

@Component({
  selector: 'jhi-tenant-fee-config-detail',
  templateUrl: './tenant-fee-config-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class TenantFeeConfigDetail {
  readonly tenantFeeConfig = input<ITenantFeeConfig | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
