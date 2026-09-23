import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { DisputeStatus } from 'app/entities/enumerations/dispute-status.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { AlertError } from 'app/shared/alert';
import { IDispute } from '../dispute.model';
import { DisputeService } from '../service/dispute.service';

import { DisputeFormGroup, DisputeFormService } from './dispute-form.service';

@Component({
  selector: 'jhi-dispute-update',
  templateUrl: './dispute-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DisputeUpdate implements OnInit {
  readonly isSaving = signal(false);
  dispute: IDispute | null = null;
  disputeStatusValues = Object.keys(DisputeStatus);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);
  transactionsSharedCollection = signal<ITransaction[]>([]);

  protected disputeService = inject(DisputeService);
  protected disputeFormService = inject(DisputeFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected transactionService = inject(TransactionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DisputeFormGroup = this.disputeFormService.createDisputeFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dispute }) => {
      this.dispute = dispute;
      if (dispute) {
        this.updateForm(dispute);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const dispute = this.disputeFormService.getDispute(this.editForm);
    if (dispute.id === null) {
      this.subscribeToSaveResponse(this.disputeService.create(dispute));
    } else {
      this.subscribeToSaveResponse(this.disputeService.update(dispute));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDispute | null>): void {
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

  protected updateForm(dispute: IDispute): void {
    this.dispute = dispute;
    this.disputeFormService.resetForm(this.editForm, dispute);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, dispute.tenant),
    );
    this.transactionsSharedCollection.update(transactions =>
      this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, dispute.transaction),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, this.dispute?.tenant),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));

    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.dispute?.transaction),
        ),
      )
      .subscribe((transactions: ITransaction[]) => this.transactionsSharedCollection.set(transactions));
  }
}
