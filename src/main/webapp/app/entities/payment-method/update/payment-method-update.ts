import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { PaymentMethodCategory } from 'app/entities/enumerations/payment-method-category.model';
import { AlertError } from 'app/shared/alert';
import { IPaymentMethod } from '../payment-method.model';
import { PaymentMethodService } from '../service/payment-method.service';

import { PaymentMethodFormGroup, PaymentMethodFormService } from './payment-method-form.service';

@Component({
  selector: 'jhi-payment-method-update',
  templateUrl: './payment-method-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PaymentMethodUpdate implements OnInit {
  readonly isSaving = signal(false);
  paymentMethod: IPaymentMethod | null = null;
  paymentMethodCategoryValues = Object.keys(PaymentMethodCategory);

  protected paymentMethodService = inject(PaymentMethodService);
  protected paymentMethodFormService = inject(PaymentMethodFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaymentMethodFormGroup = this.paymentMethodFormService.createPaymentMethodFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentMethod }) => {
      this.paymentMethod = paymentMethod;
      if (paymentMethod) {
        this.updateForm(paymentMethod);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const paymentMethod = this.paymentMethodFormService.getPaymentMethod(this.editForm);
    if (paymentMethod.id === null) {
      this.subscribeToSaveResponse(this.paymentMethodService.create(paymentMethod));
    } else {
      this.subscribeToSaveResponse(this.paymentMethodService.update(paymentMethod));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPaymentMethod | null>): void {
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

  protected updateForm(paymentMethod: IPaymentMethod): void {
    this.paymentMethod = paymentMethod;
    this.paymentMethodFormService.resetForm(this.editForm, paymentMethod);
  }
}
