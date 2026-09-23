import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { IRoutingRule } from '../routing-rule.model';
import { RoutingRuleService } from '../service/routing-rule.service';

@Component({
  templateUrl: './routing-rule-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class RoutingRuleDeleteDialog {
  routingRule?: IRoutingRule;

  protected readonly routingRuleService = inject(RoutingRuleService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.routingRuleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
