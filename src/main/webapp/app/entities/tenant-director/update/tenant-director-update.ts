import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { CountryCode } from 'app/entities/enumerations/country-code.model';
import { IdentificationType } from 'app/entities/enumerations/identification-type.model';
import { AlertError } from 'app/shared/alert';
import { TenantDirectorService } from '../service/tenant-director.service';
import { ITenantDirector } from '../tenant-director.model';

import { TenantDirectorFormGroup, TenantDirectorFormService } from './tenant-director-form.service';

@Component({
  selector: 'jhi-tenant-director-update',
  templateUrl: './tenant-director-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class TenantDirectorUpdate implements OnInit {
  readonly isSaving = signal(false);
  tenantDirector: ITenantDirector | null = null;
  countryCodeValues = Object.keys(CountryCode);
  identificationTypeValues = Object.keys(IdentificationType);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected tenantDirectorService = inject(TenantDirectorService);
  protected tenantDirectorFormService = inject(TenantDirectorFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantDirectorFormGroup = this.tenantDirectorFormService.createTenantDirectorFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantDirector }) => {
      this.tenantDirector = tenantDirector;
      if (tenantDirector) {
        this.updateForm(tenantDirector);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tenantDirector = this.tenantDirectorFormService.getTenantDirector(this.editForm);
    if (tenantDirector.id === null) {
      this.subscribeToSaveResponse(this.tenantDirectorService.create(tenantDirector));
    } else {
      this.subscribeToSaveResponse(this.tenantDirectorService.update(tenantDirector));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITenantDirector | null>): void {
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

  protected updateForm(tenantDirector: ITenantDirector): void {
    this.tenantDirector = tenantDirector;
    this.tenantDirectorFormService.resetForm(this.editForm, tenantDirector);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, tenantDirector.tenant),
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
            this.tenantDirector?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
