import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { ICountryPaymentMethod } from '../country-payment-method.model';

@Component({
  selector: 'jhi-country-payment-method-detail',
  templateUrl: './country-payment-method-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class CountryPaymentMethodDetail {
  readonly countryPaymentMethod = input<ICountryPaymentMethod | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
