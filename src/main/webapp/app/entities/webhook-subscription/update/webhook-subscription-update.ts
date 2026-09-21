import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { AlertError } from 'app/shared/alert';
import { WebhookSubscriptionService } from '../service/webhook-subscription.service';
import { IWebhookSubscription } from '../webhook-subscription.model';

import { WebhookSubscriptionFormGroup, WebhookSubscriptionFormService } from './webhook-subscription-form.service';

@Component({
  selector: 'jhi-webhook-subscription-update',
  templateUrl: './webhook-subscription-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class WebhookSubscriptionUpdate implements OnInit {
  readonly isSaving = signal(false);
  webhookSubscription: IWebhookSubscription | null = null;

  corporateTenantsSharedCollection = signal<ICorporateTenant[]>([]);

  protected webhookSubscriptionService = inject(WebhookSubscriptionService);
  protected webhookSubscriptionFormService = inject(WebhookSubscriptionFormService);
  protected corporateTenantService = inject(CorporateTenantService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WebhookSubscriptionFormGroup = this.webhookSubscriptionFormService.createWebhookSubscriptionFormGroup();

  compareCorporateTenant = (o1: ICorporateTenant | null, o2: ICorporateTenant | null): boolean =>
    this.corporateTenantService.compareCorporateTenant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ webhookSubscription }) => {
      this.webhookSubscription = webhookSubscription;
      if (webhookSubscription) {
        this.updateForm(webhookSubscription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const webhookSubscription = this.webhookSubscriptionFormService.getWebhookSubscription(this.editForm);
    if (webhookSubscription.id === null) {
      this.subscribeToSaveResponse(this.webhookSubscriptionService.create(webhookSubscription));
    } else {
      this.subscribeToSaveResponse(this.webhookSubscriptionService.update(webhookSubscription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IWebhookSubscription | null>): void {
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

  protected updateForm(webhookSubscription: IWebhookSubscription): void {
    this.webhookSubscription = webhookSubscription;
    this.webhookSubscriptionFormService.resetForm(this.editForm, webhookSubscription);

    this.corporateTenantsSharedCollection.update(corporateTenants =>
      this.corporateTenantService.addCorporateTenantToCollectionIfMissing<ICorporateTenant>(corporateTenants, webhookSubscription.tenant),
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
            this.webhookSubscription?.tenant,
          ),
        ),
      )
      .subscribe((corporateTenants: ICorporateTenant[]) => this.corporateTenantsSharedCollection.set(corporateTenants));
  }
}
