import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { IRoutingRule } from '../routing-rule.model';
import { RoutingRuleService } from '../service/routing-rule.service';

import { RoutingRuleFormService } from './routing-rule-form.service';
import { RoutingRuleUpdate } from './routing-rule-update';

describe('RoutingRule Management Update Component', () => {
  let comp: RoutingRuleUpdate;
  let fixture: ComponentFixture<RoutingRuleUpdate>;
  let activatedRoute: ActivatedRoute;
  let routingRuleFormService: RoutingRuleFormService;
  let routingRuleService: RoutingRuleService;
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

    fixture = TestBed.createComponent(RoutingRuleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    routingRuleFormService = TestBed.inject(RoutingRuleFormService);
    routingRuleService = TestBed.inject(RoutingRuleService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const routingRule: IRoutingRule = { id: 776 };
      const tenant: ICorporateTenant = { id: 10961 };
      routingRule.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ routingRule });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const routingRule: IRoutingRule = { id: 776 };
      const tenant: ICorporateTenant = { id: 10961 };
      routingRule.tenant = tenant;

      activatedRoute.data = of({ routingRule });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.routingRule).toEqual(routingRule);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRoutingRule>();
      const routingRule = { id: 9786 };
      vi.spyOn(routingRuleFormService, 'getRoutingRule').mockReturnValue(routingRule);
      vi.spyOn(routingRuleService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routingRule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(routingRule);
      saveSubject.complete();

      // THEN
      expect(routingRuleFormService.getRoutingRule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(routingRuleService.update).toHaveBeenCalledWith(expect.objectContaining(routingRule));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRoutingRule>();
      const routingRule = { id: 9786 };
      vi.spyOn(routingRuleFormService, 'getRoutingRule').mockReturnValue({ id: null });
      vi.spyOn(routingRuleService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routingRule: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(routingRule);
      saveSubject.complete();

      // THEN
      expect(routingRuleFormService.getRoutingRule).toHaveBeenCalled();
      expect(routingRuleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRoutingRule>();
      const routingRule = { id: 9786 };
      vi.spyOn(routingRuleService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ routingRule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(routingRuleService.update).toHaveBeenCalled();
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
