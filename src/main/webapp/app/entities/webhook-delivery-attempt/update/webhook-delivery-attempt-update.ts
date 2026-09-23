import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { WebhookDeliveryStatus } from 'app/entities/enumerations/webhook-delivery-status.model';
import { WebhookSubscriptionService } from 'app/entities/webhook-subscription/service/webhook-subscription.service';
import { IWebhookSubscription } from 'app/entities/webhook-subscription/webhook-subscription.model';
import { AlertError } from 'app/shared/alert';
import { WebhookDeliveryAttemptService } from '../service/webhook-delivery-attempt.service';
import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

import { WebhookDeliveryAttemptFormGroup, WebhookDeliveryAttemptFormService } from './webhook-delivery-attempt-form.service';

@Component({
  selector: 'jhi-webhook-delivery-attempt-update',
  templateUrl: './webhook-delivery-attempt-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class WebhookDeliveryAttemptUpdate implements OnInit {
  readonly isSaving = signal(false);
  webhookDeliveryAttempt: IWebhookDeliveryAttempt | null = null;
  webhookDeliveryStatusValues = Object.keys(WebhookDeliveryStatus);

  webhookSubscriptionsSharedCollection = signal<IWebhookSubscription[]>([]);

  protected webhookDeliveryAttemptService = inject(WebhookDeliveryAttemptService);
  protected webhookDeliveryAttemptFormService = inject(WebhookDeliveryAttemptFormService);
  protected webhookSubscriptionService = inject(WebhookSubscriptionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WebhookDeliveryAttemptFormGroup = this.webhookDeliveryAttemptFormService.createWebhookDeliveryAttemptFormGroup();

  compareWebhookSubscription = (o1: IWebhookSubscription | null, o2: IWebhookSubscription | null): boolean =>
    this.webhookSubscriptionService.compareWebhookSubscription(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ webhookDeliveryAttempt }) => {
      this.webhookDeliveryAttempt = webhookDeliveryAttempt;
      if (webhookDeliveryAttempt) {
        this.updateForm(webhookDeliveryAttempt);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const webhookDeliveryAttempt = this.webhookDeliveryAttemptFormService.getWebhookDeliveryAttempt(this.editForm);
    if (webhookDeliveryAttempt.id === null) {
      this.subscribeToSaveResponse(this.webhookDeliveryAttemptService.create(webhookDeliveryAttempt));
    } else {
      this.subscribeToSaveResponse(this.webhookDeliveryAttemptService.update(webhookDeliveryAttempt));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IWebhookDeliveryAttempt | null>): void {
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

  protected updateForm(webhookDeliveryAttempt: IWebhookDeliveryAttempt): void {
    this.webhookDeliveryAttempt = webhookDeliveryAttempt;
    this.webhookDeliveryAttemptFormService.resetForm(this.editForm, webhookDeliveryAttempt);

    this.webhookSubscriptionsSharedCollection.update(webhookSubscriptions =>
      this.webhookSubscriptionService.addWebhookSubscriptionToCollectionIfMissing<IWebhookSubscription>(
        webhookSubscriptions,
        webhookDeliveryAttempt.subscription,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.webhookSubscriptionService
      .query()
      .pipe(map((res: HttpResponse<IWebhookSubscription[]>) => res.body ?? []))
      .pipe(
        map((webhookSubscriptions: IWebhookSubscription[]) =>
          this.webhookSubscriptionService.addWebhookSubscriptionToCollectionIfMissing<IWebhookSubscription>(
            webhookSubscriptions,
            this.webhookDeliveryAttempt?.subscription,
          ),
        ),
      )
      .subscribe((webhookSubscriptions: IWebhookSubscription[]) => this.webhookSubscriptionsSharedCollection.set(webhookSubscriptions));
  }
}
