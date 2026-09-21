import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { SettlementBatchService } from '../service/settlement-batch.service';
import { ISettlementBatch } from '../settlement-batch.model';

import { SettlementBatchFormService } from './settlement-batch-form.service';
import { SettlementBatchUpdate } from './settlement-batch-update';

describe('SettlementBatch Management Update Component', () => {
  let comp: SettlementBatchUpdate;
  let fixture: ComponentFixture<SettlementBatchUpdate>;
  let activatedRoute: ActivatedRoute;
  let settlementBatchFormService: SettlementBatchFormService;
  let settlementBatchService: SettlementBatchService;
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

    fixture = TestBed.createComponent(SettlementBatchUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    settlementBatchFormService = TestBed.inject(SettlementBatchFormService);
    settlementBatchService = TestBed.inject(SettlementBatchService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const settlementBatch: ISettlementBatch = { id: 30023 };
      const tenant: ICorporateTenant = { id: 10961 };
      settlementBatch.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ settlementBatch });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const settlementBatch: ISettlementBatch = { id: 30023 };
      const tenant: ICorporateTenant = { id: 10961 };
      settlementBatch.tenant = tenant;

      activatedRoute.data = of({ settlementBatch });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.settlementBatch).toEqual(settlementBatch);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISettlementBatch>();
      const settlementBatch = { id: 6598 };
      vi.spyOn(settlementBatchFormService, 'getSettlementBatch').mockReturnValue(settlementBatch);
      vi.spyOn(settlementBatchService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settlementBatch });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(settlementBatch);
      saveSubject.complete();

      // THEN
      expect(settlementBatchFormService.getSettlementBatch).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(settlementBatchService.update).toHaveBeenCalledWith(expect.objectContaining(settlementBatch));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ISettlementBatch>();
      const settlementBatch = { id: 6598 };
      vi.spyOn(settlementBatchFormService, 'getSettlementBatch').mockReturnValue({ id: null });
      vi.spyOn(settlementBatchService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settlementBatch: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(settlementBatch);
      saveSubject.complete();

      // THEN
      expect(settlementBatchFormService.getSettlementBatch).toHaveBeenCalled();
      expect(settlementBatchService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ISettlementBatch>();
      const settlementBatch = { id: 6598 };
      vi.spyOn(settlementBatchService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ settlementBatch });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(settlementBatchService.update).toHaveBeenCalled();
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
