import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { AlertError } from 'app/shared/alert';
import { TenantDomainService } from '../service/tenant-domain.service';
import { ITenantDomain } from '../tenant-domain.model';

import { TenantDomainFormGroup, TenantDomainFormService } from './tenant-domain-form.service';

@Component({
  selector: 'jhi-tenant-domain-update',
  templateUrl: './tenant-domain-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TenantDomainUpdate implements OnInit {
  readonly isSaving = signal(false);
  tenantDomain: ITenantDomain | null = null;

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected tenantDomainService = inject(TenantDomainService);
  protected tenantDomainFormService = inject(TenantDomainFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantDomainFormGroup = this.tenantDomainFormService.createTenantDomainFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantDomain }) => {
      this.tenantDomain = tenantDomain;
      if (tenantDomain) {
        this.updateForm(tenantDomain);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tenantDomain = this.tenantDomainFormService.getTenantDomain(this.editForm);
    if (tenantDomain.id === null) {
      this.subscribeToSaveResponse(this.tenantDomainService.create(tenantDomain));
    } else {
      this.subscribeToSaveResponse(this.tenantDomainService.update(tenantDomain));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITenantDomain | null>): void {
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

  protected updateForm(tenantDomain: ITenantDomain): void {
    this.tenantDomain = tenantDomain;
    this.tenantDomainFormService.resetForm(this.editForm, tenantDomain);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, tenantDomain.tenant),
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
            this.tenantDomain?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
