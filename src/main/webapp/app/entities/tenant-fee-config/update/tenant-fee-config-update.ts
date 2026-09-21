import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { ICountryPaymentMethod } from 'app/entities/country-payment-method/country-payment-method.model';
import { CountryPaymentMethodService } from 'app/entities/country-payment-method/service/country-payment-method.service';
import { FeeBearer } from 'app/entities/enumerations/fee-bearer.model';
import { AlertError } from 'app/shared/alert';
import { TenantFeeConfigService } from '../service/tenant-fee-config.service';
import { ITenantFeeConfig } from '../tenant-fee-config.model';

import { TenantFeeConfigFormGroup, TenantFeeConfigFormService } from './tenant-fee-config-form.service';

@Component({
  selector: 'jhi-tenant-fee-config-update',
  templateUrl: './tenant-fee-config-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TenantFeeConfigUpdate implements OnInit {
  readonly isSaving = signal(false);
  tenantFeeConfig: ITenantFeeConfig | null = null;
  feeBearerValues = Object.keys(FeeBearer);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);
  countryPaymentMethodsSharedCollection = signal<ICountryPaymentMethod[]>([]);

  protected tenantFeeConfigService = inject(TenantFeeConfigService);
  protected tenantFeeConfigFormService = inject(TenantFeeConfigFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected countryPaymentMethodService = inject(CountryPaymentMethodService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TenantFeeConfigFormGroup = this.tenantFeeConfigFormService.createTenantFeeConfigFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  compareCountryPaymentMethod = (o1: ICountryPaymentMethod | null, o2: ICountryPaymentMethod | null): boolean =>
    this.countryPaymentMethodService.compareCountryPaymentMethod(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantFeeConfig }) => {
      this.tenantFeeConfig = tenantFeeConfig;
      if (tenantFeeConfig) {
        this.updateForm(tenantFeeConfig);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const tenantFeeConfig = this.tenantFeeConfigFormService.getTenantFeeConfig(this.editForm);
    if (tenantFeeConfig.id === null) {
      this.subscribeToSaveResponse(this.tenantFeeConfigService.create(tenantFeeConfig));
    } else {
      this.subscribeToSaveResponse(this.tenantFeeConfigService.update(tenantFeeConfig));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITenantFeeConfig | null>): void {
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

  protected updateForm(tenantFeeConfig: ITenantFeeConfig): void {
    this.tenantFeeConfig = tenantFeeConfig;
    this.tenantFeeConfigFormService.resetForm(this.editForm, tenantFeeConfig);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, tenantFeeConfig.tenant),
    );
    this.countryPaymentMethodsSharedCollection.update(countryPaymentMethods =>
      this.countryPaymentMethodService.addCountryPaymentMethodToCollectionIfMissing<ICountryPaymentMethod>(
        countryPaymentMethods,
        tenantFeeConfig.countryPaymentMethod,
      ),
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
            this.tenantFeeConfig?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));

    this.countryPaymentMethodService
      .query()
      .pipe(map((res: HttpResponse<ICountryPaymentMethod[]>) => res.body ?? []))
      .pipe(
        map((countryPaymentMethods: ICountryPaymentMethod[]) =>
          this.countryPaymentMethodService.addCountryPaymentMethodToCollectionIfMissing<ICountryPaymentMethod>(
            countryPaymentMethods,
            this.tenantFeeConfig?.countryPaymentMethod,
          ),
        ),
      )
      .subscribe((countryPaymentMethods: ICountryPaymentMethod[]) => this.countryPaymentMethodsSharedCollection.set(countryPaymentMethods));
  }
}
