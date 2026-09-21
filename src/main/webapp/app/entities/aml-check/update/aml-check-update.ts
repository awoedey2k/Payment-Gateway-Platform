import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { AmlDecision } from 'app/entities/enumerations/aml-decision.model';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { AlertError } from 'app/shared/alert';
import { IAmlCheck } from '../aml-check.model';
import { AmlCheckService } from '../service/aml-check.service';

import { AmlCheckFormGroup, AmlCheckFormService } from './aml-check-form.service';

@Component({
  selector: 'jhi-aml-check-update',
  templateUrl: './aml-check-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AmlCheckUpdate implements OnInit {
  readonly isSaving = signal(false);
  amlCheck: IAmlCheck | null = null;
  amlDecisionValues = Object.keys(AmlDecision);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);
  transactionsSharedCollection = signal<ITransaction[]>([]);

  protected amlCheckService = inject(AmlCheckService);
  protected amlCheckFormService = inject(AmlCheckFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected transactionService = inject(TransactionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AmlCheckFormGroup = this.amlCheckFormService.createAmlCheckFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  compareTransaction = (o1: ITransaction | null, o2: ITransaction | null): boolean => this.transactionService.compareTransaction(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ amlCheck }) => {
      this.amlCheck = amlCheck;
      if (amlCheck) {
        this.updateForm(amlCheck);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const amlCheck = this.amlCheckFormService.getAmlCheck(this.editForm);
    if (amlCheck.id === null) {
      this.subscribeToSaveResponse(this.amlCheckService.create(amlCheck));
    } else {
      this.subscribeToSaveResponse(this.amlCheckService.update(amlCheck));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAmlCheck | null>): void {
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

  protected updateForm(amlCheck: IAmlCheck): void {
    this.amlCheck = amlCheck;
    this.amlCheckFormService.resetForm(this.editForm, amlCheck);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, amlCheck.tenant),
    );
    this.transactionsSharedCollection.update(transactions =>
      this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, amlCheck.transaction),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, this.amlCheck?.tenant),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));

    this.transactionService
      .query()
      .pipe(map((res: HttpResponse<ITransaction[]>) => res.body ?? []))
      .pipe(
        map((transactions: ITransaction[]) =>
          this.transactionService.addTransactionToCollectionIfMissing<ITransaction>(transactions, this.amlCheck?.transaction),
        ),
      )
      .subscribe((transactions: ITransaction[]) => this.transactionsSharedCollection.set(transactions));
  }
}
