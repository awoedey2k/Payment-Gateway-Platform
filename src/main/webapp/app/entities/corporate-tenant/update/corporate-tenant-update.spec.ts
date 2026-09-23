import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from '../corporate-tenant.model';
import { CorporateTenantService } from '../service/corporate-tenant.service';

import { CorporateTenantFormService } from './corporate-tenant-form.service';
import { CorporateTenantUpdate } from './corporate-tenant-update';

describe('CorporateTenant Management Update Component', () => {
  let comp: CorporateTenantUpdate;
  let fixture: ComponentFixture<CorporateTenantUpdate>;
  let activatedRoute: ActivatedRoute;
  let corporateTenantFormService: CorporateTenantFormService;
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

    fixture = TestBed.createComponent(CorporateTenantUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    corporateTenantFormService = TestBed.inject(CorporateTenantFormService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const corporateTenant: ICorporateTenant = { id: 28703 };

      activatedRoute.data = of({ corporateTenant });
      comp.ngOnInit();

      expect(comp.corporateTenant).toEqual(corporateTenant);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICorporateTenant>();
      const corporateTenant = { id: 10961 };
      vi.spyOn(corporateTenantFormService, 'getCorporateTenant').mockReturnValue(corporateTenant);
      vi.spyOn(corporateTenantService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ corporateTenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(corporateTenant);
      saveSubject.complete();

      // THEN
      expect(corporateTenantFormService.getCorporateTenant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(corporateTenantService.update).toHaveBeenCalledWith(expect.objectContaining(corporateTenant));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICorporateTenant>();
      const corporateTenant = { id: 10961 };
      vi.spyOn(corporateTenantFormService, 'getCorporateTenant').mockReturnValue({ id: null });
      vi.spyOn(corporateTenantService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ corporateTenant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(corporateTenant);
      saveSubject.complete();

      // THEN
      expect(corporateTenantFormService.getCorporateTenant).toHaveBeenCalled();
      expect(corporateTenantService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICorporateTenant>();
      const corporateTenant = { id: 10961 };
      vi.spyOn(corporateTenantService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ corporateTenant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(corporateTenantService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
