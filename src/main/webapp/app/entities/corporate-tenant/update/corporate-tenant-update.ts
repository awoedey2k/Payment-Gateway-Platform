import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { CountryCode } from 'app/entities/enumerations/country-code.model';
import { KycStatus } from 'app/entities/enumerations/kyc-status.model';
import { TenantStatus } from 'app/entities/enumerations/tenant-status.model';
import { AlertError } from 'app/shared/alert';
import { ICorporateTenant } from '../corporate-tenant.model';
import { CorporateTenantService } from '../service/corporate-tenant.service';

import { CorporateTenantFormGroup, CorporateTenantFormService } from './corporate-tenant-form.service';

@Component({
  selector: 'jhi-corporate-tenant-update',
  templateUrl: './corporate-tenant-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CorporateTenantUpdate implements OnInit {
  readonly isSaving = signal(false);
  corporateTenant: ICorporateTenant | null = null;
  countryCodeValues = Object.keys(CountryCode);
  tenantStatusValues = Object.keys(TenantStatus);
  kycStatusValues = Object.keys(KycStatus);

  protected corporateTenantService = inject(CorporateTenantService);
  protected corporateTenantFormService = inject(CorporateTenantFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CorporateTenantFormGroup = this.corporateTenantFormService.createCorporateTenantFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ corporateTenant }) => {
      this.corporateTenant = corporateTenant;
      if (corporateTenant) {
        this.updateForm(corporateTenant);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const corporateTenant = this.corporateTenantFormService.getCorporateTenant(this.editForm);
    if (corporateTenant.id === null) {
      this.subscribeToSaveResponse(this.corporateTenantService.create(corporateTenant));
    } else {
      this.subscribeToSaveResponse(this.corporateTenantService.update(corporateTenant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICorporateTenant | null>): void {
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

  protected updateForm(corporateTenant: ICorporateTenant): void {
    this.corporateTenant = corporateTenant;
    this.corporateTenantFormService.resetForm(this.editForm, corporateTenant);
  }
}
