import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { ICurrency } from '../currency.model';
import { CurrencyService } from '../service/currency.service';

import { CurrencyFormGroup, CurrencyFormService } from './currency-form.service';

@Component({
  selector: 'jhi-currency-update',
  templateUrl: './currency-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CurrencyUpdate implements OnInit {
  readonly isSaving = signal(false);
  currency: ICurrency | null = null;

  protected currencyService = inject(CurrencyService);
  protected currencyFormService = inject(CurrencyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CurrencyFormGroup = this.currencyFormService.createCurrencyFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ currency }) => {
      this.currency = currency;
      if (currency) {
        this.updateForm(currency);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const currency = this.currencyFormService.getCurrency(this.editForm);
    if (currency.id === null) {
      this.subscribeToSaveResponse(this.currencyService.create(currency));
    } else {
      this.subscribeToSaveResponse(this.currencyService.update(currency));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICurrency | null>): void {
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

  protected updateForm(currency: ICurrency): void {
    this.currency = currency;
    this.currencyFormService.resetForm(this.editForm, currency);
  }
}
