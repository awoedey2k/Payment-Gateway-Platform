import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { WebhookSubscriptionService } from '../service/webhook-subscription.service';
import { IWebhookSubscription } from '../webhook-subscription.model';

import { WebhookSubscriptionFormService } from './webhook-subscription-form.service';
import { WebhookSubscriptionUpdate } from './webhook-subscription-update';

describe('WebhookSubscription Management Update Component', () => {
  let comp: WebhookSubscriptionUpdate;
  let fixture: ComponentFixture<WebhookSubscriptionUpdate>;
  let activatedRoute: ActivatedRoute;
  let webhookSubscriptionFormService: WebhookSubscriptionFormService;
  let webhookSubscriptionService: WebhookSubscriptionService;
  let corporateTenantService: CorporateTenantService;

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

    fixture = TestBed.createComponent(WebhookSubscriptionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    webhookSubscriptionFormService = TestBed.inject(WebhookSubscriptionFormService);
    webhookSubscriptionService = TestBed.inject(WebhookSubscriptionService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const webhookSubscription: IWebhookSubscription = { id: 24725 };
      const tenant: ICorporateTenant = { id: 10961 };
      webhookSubscription.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ webhookSubscription });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const webhookSubscription: IWebhookSubscription = { id: 24725 };
      const tenant: ICorporateTenant = { id: 10961 };
      webhookSubscription.tenant = tenant;

      activatedRoute.data = of({ webhookSubscription });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.webhookSubscription).toEqual(webhookSubscription);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookSubscription>();
      const webhookSubscription = { id: 28283 };
      vi.spyOn(webhookSubscriptionFormService, 'getWebhookSubscription').mockReturnValue(webhookSubscription);
      vi.spyOn(webhookSubscriptionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(webhookSubscription);
      saveSubject.complete();

      // THEN
      expect(webhookSubscriptionFormService.getWebhookSubscription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(webhookSubscriptionService.update).toHaveBeenCalledWith(expect.objectContaining(webhookSubscription));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookSubscription>();
      const webhookSubscription = { id: 28283 };
      vi.spyOn(webhookSubscriptionFormService, 'getWebhookSubscription').mockReturnValue({ id: null });
      vi.spyOn(webhookSubscriptionService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookSubscription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(webhookSubscription);
      saveSubject.complete();

      // THEN
      expect(webhookSubscriptionFormService.getWebhookSubscription).toHaveBeenCalled();
      expect(webhookSubscriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IWebhookSubscription>();
      const webhookSubscription = { id: 28283 };
      vi.spyOn(webhookSubscriptionService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ webhookSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(webhookSubscriptionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCorporateTenant', () => {
      it('should forward to corporateTenantService', () => {
        const entity = { id: 10961 };
        const entity2 = { id: 28703 };
        vi.spyOn(corporateTenantService, 'compareCorporateTenant');
        comp.compareCorporateTenant(entity, entity2);
        expect(corporateTenantService.compareCorporateTenant).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
