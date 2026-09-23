import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { SettlementBatchStatus } from 'app/entities/enumerations/settlement-batch-status.model';
import { AlertError } from 'app/shared/alert';
import { SettlementBatchService } from '../service/settlement-batch.service';
import { ISettlementBatch } from '../settlement-batch.model';

import { SettlementBatchFormGroup, SettlementBatchFormService } from './settlement-batch-form.service';

@Component({
  selector: 'jhi-settlement-batch-update',
  templateUrl: './settlement-batch-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class SettlementBatchUpdate implements OnInit {
  readonly isSaving = signal(false);
  settlementBatch: ISettlementBatch | null = null;
  settlementBatchStatusValues = Object.keys(SettlementBatchStatus);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected settlementBatchService = inject(SettlementBatchService);
  protected settlementBatchFormService = inject(SettlementBatchFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SettlementBatchFormGroup = this.settlementBatchFormService.createSettlementBatchFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ settlementBatch }) => {
      this.settlementBatch = settlementBatch;
      if (settlementBatch) {
        this.updateForm(settlementBatch);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const settlementBatch = this.settlementBatchFormService.getSettlementBatch(this.editForm);
    if (settlementBatch.id === null) {
      this.subscribeToSaveResponse(this.settlementBatchService.create(settlementBatch));
    } else {
      this.subscribeToSaveResponse(this.settlementBatchService.update(settlementBatch));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ISettlementBatch | null>): void {
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

  protected updateForm(settlementBatch: ISettlementBatch): void {
    this.settlementBatch = settlementBatch;
    this.settlementBatchFormService.resetForm(this.editForm, settlementBatch);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, settlementBatch.tenant),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(
            corporateTenants,
            this.settlementBatch?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
