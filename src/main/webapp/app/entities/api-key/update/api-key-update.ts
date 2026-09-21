import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { ApiEnvironment } from 'app/entities/enumerations/api-environment.model';
import { AlertError } from 'app/shared/alert';
import { IApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

import { ApiKeyFormGroup, ApiKeyFormService } from './api-key-form.service';

@Component({
  selector: 'jhi-api-key-update',
  templateUrl: './api-key-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ApiKeyUpdate implements OnInit {
  readonly isSaving = signal(false);
  apiKey: IApiKey | null = null;
  apiEnvironmentValues = Object.keys(ApiEnvironment);

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected apiKeyService = inject(ApiKeyService);
  protected apiKeyFormService = inject(ApiKeyFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ApiKeyFormGroup = this.apiKeyFormService.createApiKeyFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apiKey }) => {
      this.apiKey = apiKey;
      if (apiKey) {
        this.updateForm(apiKey);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const apiKey = this.apiKeyFormService.getApiKey(this.editForm);
    if (apiKey.id === null) {
      this.subscribeToSaveResponse(this.apiKeyService.create(apiKey));
    } else {
      this.subscribeToSaveResponse(this.apiKeyService.update(apiKey));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IApiKey | null>): void {
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

  protected updateForm(apiKey: IApiKey): void {
    this.apiKey = apiKey;
    this.apiKeyFormService.resetForm(this.editForm, apiKey);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, apiKey.tenant),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.corporateTenantService
      .query()
      .pipe(map((res: HttpResponse<ICorporateTenant[]>) => res.body ?? []))
      .pipe(
        map((corporateTenants: ICorporateTenant[]) =>
          this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, this.apiKey?.tenant),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
