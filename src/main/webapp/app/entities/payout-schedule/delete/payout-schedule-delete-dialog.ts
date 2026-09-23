import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IPayoutSchedule } from '../payout-schedule.model';
import { PayoutScheduleService } from '../service/payout-schedule.service';

@Component({
  templateUrl: './payout-schedule-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class PayoutScheduleDeleteDialog {
  payoutSchedule?: IPayoutSchedule;

  protected readonly payoutScheduleService = inject(PayoutScheduleService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.payoutScheduleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
