import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { WebhookSubscriptionService } from 'app/entities/webhook-subscription/service/webhook-subscription.service';
import { IWebhookSubscription } from 'app/entities/webhook-subscription/webhook-subscription.model';
import { WebhookDeliveryAttemptService } from '../service/webhook-delivery-attempt.service';
import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';

import { WebhookDeliveryAttemptFormService } from './webhook-delivery-attempt-form.service';
import { WebhookDeliveryAttemptUpdate } from './webhook-delivery-attempt-update';

describe('WebhookDeliveryAttempt Management Update Component', () => {
  let comp: WebhookDeliveryAttemptUpdate;
  let fixture: ComponentFixture<WebhookDeliveryAttemptUpdate>;
  let activatedRoute: ActivatedRoute;
  let webhookDeliveryAttemptFormService: WebhookDeliveryAttemptFormService;
  let webhookDeliveryAttemptService: WebhookDeliveryAttemptService;
  let webhookSubscriptionService: WebhookSubscriptionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(WebhookDeliveryAttemptUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    webhookDeliveryAttemptFormService = TestBed.inject(WebhookDeliveryAttemptFormService);
    webhookDeliveryAttemptService = TestBed.inject(WebhookDeliveryAttemptService);
    webhookSubscriptionService = TestBed.inject(WebhookSubscriptionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call WebhookSubscription query and add missing value', () => {
      const webhookDeliveryAttempt: IWebhookDeliveryAttempt = { id: 17935 };
      const subscription: IWebhookSubscription = { id: 28283 };
      webhookDeliveryAttempt.subscription = subscription;

      const webhookSubscriptionCollection: IWebhookSubscription[] = [{ id: 28283 }];
      vi.spyOn(webhookSubscriptionService, 'query').mockReturnValue(of(new HttpResponse({ body: webhookSubscriptionCollection })));
      const additionalWebhookSubscriptions = [subscription];
      const expectedCollection: IWebhookSubscription[] = [...additionalWebhookSubscriptions, ...webhookSubscriptionCollection];
      vi.spyOn(webhookSubscriptionService, 'addWebhookSubscriptionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ webhookDeliveryAttempt });
      comp.ngOnInit();

      expect(webhookSubscriptionService.query).toHaveBeenCalled();
      expect(webhookSubscriptionService.addWebhookSubscriptionToCollectionIfMissing).toHaveBeenCalledWith(
        webhookSubscriptionCollection,
        ...additionalWebhookSubscriptions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.webhookSubscriptionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const webhookDeliveryAttempt: IWebhookDeliveryAttempt = { id: 17935 };
      const subscription: IWebhookSubscription = { id: 28283 };
      webhookDeliveryAttempt.subscription = subscription;

      activatedRoute.data = of({ webhookDeliveryAttempt });
      comp.ngOnInit();

      expect(comp.webhookSubscriptionsSharedCollection()).toContainEqual(subscription);
      expect(comp.webhookDeliveryAttempt).toEqual(webhookDeliveryAttempt);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookDeliveryAttempt>();
      const webhookDeliveryAttempt = { id: 3237 };
      vi.spyOn(webhookDeliveryAttemptFormService, 'getWebhookDeliveryAttempt').mockReturnValue(webhookDeliveryAttempt);
      vi.spyOn(webhookDeliveryAttemptService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookDeliveryAttempt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(webhookDeliveryAttempt);
      saveSubject.complete();

      // THEN
      expect(webhookDeliveryAttemptFormService.getWebhookDeliveryAttempt).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(webhookDeliveryAttemptService.update).toHaveBeenCalledWith(expect.objectContaining(webhookDeliveryAttempt));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookDeliveryAttempt>();
      const webhookDeliveryAttempt = { id: 3237 };
      vi.spyOn(webhookDeliveryAttemptFormService, 'getWebhookDeliveryAttempt').mockReturnValue({ id: null });
      vi.spyOn(webhookDeliveryAttemptService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookDeliveryAttempt: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(webhookDeliveryAttempt);
      saveSubject.complete();

      // THEN
      expect(webhookDeliveryAttemptFormService.getWebhookDeliveryAttempt).toHaveBeenCalled();
      expect(webhookDeliveryAttemptService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookDeliveryAttempt>();
      const webhookDeliveryAttempt = { id: 3237 };
      vi.spyOn(webhookDeliveryAttemptService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookDeliveryAttempt });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(webhookDeliveryAttemptService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWebhookSubscription', () => {
      it('should forward to webhookSubscriptionService', () => {
        const entity = { id: 28283 };
        const entity2 = { id: 24725 };
        vi.spyOn(webhookSubscriptionService, 'compareWebhookSubscription');
        comp.compareWebhookSubscription(entity, entity2);
        expect(webhookSubscriptionService.compareWebhookSubscription).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
