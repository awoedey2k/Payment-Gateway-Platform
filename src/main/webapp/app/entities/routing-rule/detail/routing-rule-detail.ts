import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IRoutingRule } from '../routing-rule.model';

@Component({
  selector: 'jhi-routing-rule-detail',
  templateUrl: './routing-rule-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class RoutingRuleDetail {
  readonly routingRule = input<IRoutingRule | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
