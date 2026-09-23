import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { RefundReason } from 'app/entities/enumerations/refund-reason.model';
import { RefundStatus } from 'app/entities/enumerations/refund-status.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { AlertError } from 'app/shared/alert';
import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';

import { RefundFormGroup, RefundFormService } from './refund-form.service';

@Component({
  selector: 'jhi-refund-update',
  templateUrl: './refund-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RefundUpdate implements OnInit {
  readonly isSaving = signal(false);
  refund: IRefund | null = null;
  refundReasonValues = Object.keys(RefundReason);
  refundStatusValues = Object.keys(RefundStatus);

  transactionsSharedCollection = signal<ITransaction[]>([]);

  protected refundService = inject(RefundService);
  protected refundFormService = inject(RefundFormService);
  protected transactionService = inject(TransactionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RefundFormGroup = this.refundFormService.createRefundFormGroup();

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refund }) => {
      this.refund = refund;
      if (refund) {
        this.updateForm(refund);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const refund = this.refundFormService.getRefund(this.editForm);
    if (refund.id === null) {
      this.subscribeToSaveResponse(this.refundService.create(refund));
    } else {
      this.subscribeToSaveResponse(this.refundService.update(refund));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRefund | null>): void {
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

  protected updateForm(refund: IRefund): void {
    this.refund = refund;
    this.refundFormService.resetForm(this.editForm, refund);

    this.transactionsSharedCollection.update(transactions =>
      this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, refund.transaction),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.refund?.transaction),
        ),
      )
      .subscribe((transactions: ITransaction[]) => this.transactionsSharedCollection.set(transactions));
  }
}
