import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { PayoutFrequency } from 'app/entities/enumerations/payout-frequency.model';
import { AlertError } from 'app/shared/alert';
import { IPayoutSchedule } from '../payout-schedule.model';
import { PayoutScheduleService } from '../service/payout-schedule.service';

import { PayoutScheduleFormGroup, PayoutScheduleFormService } from './payout-schedule-form.service';

@Component({
  selector: 'jhi-payout-schedule-update',
  templateUrl: './payout-schedule-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PayoutScheduleUpdate implements OnInit {
  readonly isSaving = signal(false);
  payoutSchedule: IPayoutSchedule | null = null;
  payoutFrequencyValues = Object.keys(PayoutFrequency);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected payoutScheduleService = inject(PayoutScheduleService);
  protected payoutScheduleFormService = inject(PayoutScheduleFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PayoutScheduleFormGroup = this.payoutScheduleFormService.createPayoutScheduleFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payoutSchedule }) => {
      this.payoutSchedule = payoutSchedule;
      if (payoutSchedule) {
        this.updateForm(payoutSchedule);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const payoutSchedule = this.payoutScheduleFormService.getPayoutSchedule(this.editForm);
    if (payoutSchedule.id === null) {
      this.subscribeToSaveResponse(this.payoutScheduleService.create(payoutSchedule));
    } else {
      this.subscribeToSaveResponse(this.payoutScheduleService.update(payoutSchedule));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPayoutSchedule | null>): void {
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

  protected updateForm(payoutSchedule: IPayoutSchedule): void {
    this.payoutSchedule = payoutSchedule;
    this.payoutScheduleFormService.resetForm(this.editForm, payoutSchedule);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, payoutSchedule.tenant),
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
            this.payoutSchedule?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
