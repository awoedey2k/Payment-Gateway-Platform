import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { TenantDirectorService } from '../service/tenant-director.service';
import { ITenantDirector } from '../tenant-director.model';

import { TenantDirectorFormService } from './tenant-director-form.service';
import { TenantDirectorUpdate } from './tenant-director-update';

describe('TenantDirector Management Update Component', () => {
  let comp: TenantDirectorUpdate;
  let fixture: ComponentFixture<TenantDirectorUpdate>;
  let activatedRoute: ActivatedRoute;
  let tenantDirectorFormService: TenantDirectorFormService;
  let tenantDirectorService: TenantDirectorService;
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

    fixture = TestBed.createComponent(TenantDirectorUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantDirectorFormService = TestBed.inject(TenantDirectorFormService);
    tenantDirectorService = TestBed.inject(TenantDirectorService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const tenantDirector: ITenantDirector = { id: 25906 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantDirector.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenantDirector });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tenantDirector: ITenantDirector = { id: 25906 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantDirector.tenant = tenant;

      activatedRoute.data = of({ tenantDirector });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.tenantDirector).toEqual(tenantDirector);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDirector>();
      const tenantDirector = { id: 7225 };
      vi.spyOn(tenantDirectorFormService, 'getTenantDirector').mockReturnValue(tenantDirector);
      vi.spyOn(tenantDirectorService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDirector });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantDirector);
      saveSubject.complete();

      // THEN
      expect(tenantDirectorFormService.getTenantDirector).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantDirectorService.update).toHaveBeenCalledWith(expect.objectContaining(tenantDirector));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDirector>();
      const tenantDirector = { id: 7225 };
      vi.spyOn(tenantDirectorFormService, 'getTenantDirector').mockReturnValue({ id: null });
      vi.spyOn(tenantDirectorService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDirector: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantDirector);
      saveSubject.complete();

      // THEN
      expect(tenantDirectorFormService.getTenantDirector).toHaveBeenCalled();
      expect(tenantDirectorService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDirector>();
      const tenantDirector = { id: 7225 };
      vi.spyOn(tenantDirectorService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDirector });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantDirectorService.update).toHaveBeenCalled();
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
