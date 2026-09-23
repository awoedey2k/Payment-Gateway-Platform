import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { IPaymentMethod } from 'app/entities/payment-method/payment-method.model';
import { PaymentMethodService } from 'app/entities/payment-method/service/payment-method.service';
import { AlertError } from 'app/shared/alert';
import { ICountryPaymentMethod } from '../country-payment-method.model';
import { CountryPaymentMethodService } from '../service/country-payment-method.service';

import { CountryPaymentMethodFormGroup, CountryPaymentMethodFormService } from './country-payment-method-form.service';

@Component({
  selector: 'jhi-country-payment-method-update',
  templateUrl: './country-payment-method-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CountryPaymentMethodUpdate implements OnInit {
  readonly isSaving = signal(false);
  countryPaymentMethod: ICountryPaymentMethod | null = null;

  countriesSharedCollection = signal<ICountry[]>([]);
  paymentMethodsSharedCollection = signal<IPaymentMethod[]>([]);

  protected countryPaymentMethodService = inject(CountryPaymentMethodService);
  protected countryPaymentMethodFormService = inject(CountryPaymentMethodFormService);
  protected countryService = inject(CountryService);
  protected paymentMethodService = inject(PaymentMethodService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CountryPaymentMethodFormGroup = this.countryPaymentMethodFormService.createCountryPaymentMethodFormGroup();

  compareCountry = (o1: ICountry | null, o2: ICountry | null): boolean => this.countryService.compareCountry(o1, o2);

  comparePaymentMethod = (o1: IPaymentMethod | null, o2: IPaymentMethod | null): boolean =>
    this.paymentMethodService.comparePaymentMethod(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ countryPaymentMethod }) => {
      this.countryPaymentMethod = countryPaymentMethod;
      if (countryPaymentMethod) {
        this.updateForm(countryPaymentMethod);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const countryPaymentMethod = this.countryPaymentMethodFormService.getCountryPaymentMethod(this.editForm);
    if (countryPaymentMethod.id === null) {
      this.subscribeToSaveResponse(this.countryPaymentMethodService.create(countryPaymentMethod));
    } else {
      this.subscribeToSaveResponse(this.countryPaymentMethodService.update(countryPaymentMethod));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICountryPaymentMethod | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(countryPaymentMethod: ICountryPaymentMethod): void {
    this.countryPaymentMethod = countryPaymentMethod;
    this.countryPaymentMethodFormService.resetForm(this.editForm, countryPaymentMethod);

    this.countriesSharedCollection.update(countries =>
      this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, countryPaymentMethod.country),
    );
    this.paymentMethodsSharedCollection.update(paymentMethods =>
      this.paymentMethodService.addPaymentMethodToCollectionIfMissing<IPaymentMethod>(paymentMethods, countryPaymentMethod.paymentMethod),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.countryService
      .query()
      .pipe(map((res: HttpResponse<ICountry[]>) => res.body ?? []))
      .pipe(
        map((countries: ICountry[]) =>
          this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, this.countryPaymentMethod?.country),
        ),
      )
      .subscribe((countries: ICountry[]) => this.countriesSharedCollection.set(countries));

    this.paymentMethodService
      .query()
      .pipe(map((res: HttpResponse<IPaymentMethod[]>) => res.body ?? []))
      .pipe(
        map((paymentMethods: IPaymentMethod[]) =>
          this.paymentMethodService.addPaymentMethodToCollectionIfMissing<IPaymentMethod>(
            paymentMethods,
            this.countryPaymentMethod?.paymentMethod,
          ),
        ),
      )
      .subscribe((paymentMethods: IPaymentMethod[]) => this.paymentMethodsSharedCollection.set(paymentMethods));
  }
}
