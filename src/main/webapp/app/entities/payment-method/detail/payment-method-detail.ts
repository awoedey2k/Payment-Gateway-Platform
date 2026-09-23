import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { IPaymentMethod } from '../payment-method.model';

@Component({
  selector: 'jhi-payment-method-detail',
  templateUrl: './payment-method-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class PaymentMethodDetail {
  readonly paymentMethod = input<IPaymentMethod | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
