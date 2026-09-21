import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { IPayoutSchedule } from '../payout-schedule.model';
import { PayoutScheduleService } from '../service/payout-schedule.service';

import { PayoutScheduleFormService } from './payout-schedule-form.service';
import { PayoutScheduleUpdate } from './payout-schedule-update';

describe('PayoutSchedule Management Update Component', () => {
  let comp: PayoutScheduleUpdate;
  let fixture: ComponentFixture<PayoutScheduleUpdate>;
  let activatedRoute: ActivatedRoute;
  let payoutScheduleFormService: PayoutScheduleFormService;
  let payoutScheduleService: PayoutScheduleService;
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

    fixture = TestBed.createComponent(PayoutScheduleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    payoutScheduleFormService = TestBed.inject(PayoutScheduleFormService);
    payoutScheduleService = TestBed.inject(PayoutScheduleService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const payoutSchedule: IPayoutSchedule = { id: 2981 };
      const tenant: ICorporateTenant = { id: 10961 };
      payoutSchedule.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ payoutSchedule });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const payoutSchedule: IPayoutSchedule = { id: 2981 };
      const tenant: ICorporateTenant = { id: 10961 };
      payoutSchedule.tenant = tenant;

      activatedRoute.data = of({ payoutSchedule });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.payoutSchedule).toEqual(payoutSchedule);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPayoutSchedule>();
      const payoutSchedule = { id: 4713 };
      vi.spyOn(payoutScheduleFormService, 'getPayoutSchedule').mockReturnValue(payoutSchedule);
      vi.spyOn(payoutScheduleService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payoutSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(payoutSchedule);
      saveSubject.complete();

      // THEN
      expect(payoutScheduleFormService.getPayoutSchedule).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(payoutScheduleService.update).toHaveBeenCalledWith(expect.objectContaining(payoutSchedule));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPayoutSchedule>();
      const payoutSchedule = { id: 4713 };
      vi.spyOn(payoutScheduleFormService, 'getPayoutSchedule').mockReturnValue({ id: null });
      vi.spyOn(payoutScheduleService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payoutSchedule: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(payoutSchedule);
      saveSubject.complete();

      // THEN
      expect(payoutScheduleFormService.getPayoutSchedule).toHaveBeenCalled();
      expect(payoutScheduleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPayoutSchedule>();
      const payoutSchedule = { id: 4713 };
      vi.spyOn(payoutScheduleService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ payoutSchedule });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(payoutScheduleService.update).toHaveBeenCalled();
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
