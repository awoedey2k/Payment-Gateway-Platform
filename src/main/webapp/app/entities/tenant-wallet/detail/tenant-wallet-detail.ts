import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ITenantWallet } from '../tenant-wallet.model';

@Component({
  selector: 'jhi-tenant-wallet-detail',
  templateUrl: './tenant-wallet-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class TenantWalletDetail {
  readonly tenantWallet = input<ITenantWallet | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
