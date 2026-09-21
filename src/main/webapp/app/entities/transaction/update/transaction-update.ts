import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { TransactionStatus } from 'app/entities/enumerations/transaction-status.model';
import { AlertError } from 'app/shared/alert';
import { TransactionService } from '../service/transaction.service';
import { ITransaction } from '../transaction.model';

import { TransactionFormGroup, TransactionFormService } from './transaction-form.service';

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TransactionUpdate implements OnInit {
  readonly isSaving = signal(false);
  transaction: ITransaction | null = null;
  transactionStatusValues = Object.keys(TransactionStatus);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected transactionService = inject(TransactionService);
  protected transactionFormService = inject(TransactionFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TransactionFormGroup = this.transactionFormService.createTransactionFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.transaction = transaction;
      if (transaction) {
        this.updateForm(transaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const transaction = this.transactionFormService.getTransaction(this.editForm);
    if (transaction.id === null) {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITransaction | null>): void {
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

  protected updateForm(transaction: ITransaction): void {
    this.transaction = transaction;
    this.transactionFormService.resetForm(this.editForm, transaction);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, transaction.tenant),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, this.transaction?.tenant),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
