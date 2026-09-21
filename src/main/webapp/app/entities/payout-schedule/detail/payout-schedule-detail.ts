import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IPayoutSchedule } from '../payout-schedule.model';

@Component({
  selector: 'jhi-payout-schedule-detail',
  templateUrl: './payout-schedule-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class PayoutScheduleDetail {
  readonly payoutSchedule = input<IPayoutSchedule | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
