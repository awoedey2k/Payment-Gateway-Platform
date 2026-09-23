import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { AlertError } from 'app/shared/alert';
import { TenantWalletService } from '../service/tenant-wallet.service';
import { ITenantWallet } from '../tenant-wallet.model';

import { TenantWalletFormGroup, TenantWalletFormService } from './tenant-wallet-form.service';

@Component({
  selector: 'jhi-tenant-wallet-update',
  templateUrl: './tenant-wallet-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TenantWalletUpdate implements OnInit {
  readonly isSaving = signal(false);
  tenantWallet: ITenantWallet | null = null;

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected tenantWalletService = inject(TenantWalletService);
  protected tenantWalletFormService = inject(TenantWalletFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantWalletFormGroup = this.tenantWalletFormService.createTenantWalletFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantWallet }) => {
      this.tenantWallet = tenantWallet;
      if (tenantWallet) {
        this.updateForm(tenantWallet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tenantWallet = this.tenantWalletFormService.getTenantWallet(this.editForm);
    if (tenantWallet.id === null) {
      this.subscribeToSaveResponse(this.tenantWalletService.create(tenantWallet));
    } else {
      this.subscribeToSaveResponse(this.tenantWalletService.update(tenantWallet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITenantWallet | null>): void {
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

  protected updateForm(tenantWallet: ITenantWallet): void {
    this.tenantWallet = tenantWallet;
    this.tenantWalletFormService.resetForm(this.editForm, tenantWallet);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, tenantWallet.tenant),
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
            this.tenantWallet?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
