import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { TenantDomainService } from '../service/tenant-domain.service';
import { ITenantDomain } from '../tenant-domain.model';

import { TenantDomainFormService } from './tenant-domain-form.service';
import { TenantDomainUpdate } from './tenant-domain-update';

describe('TenantDomain Management Update Component', () => {
  let comp: TenantDomainUpdate;
  let fixture: ComponentFixture<TenantDomainUpdate>;
  let activatedRoute: ActivatedRoute;
  let tenantDomainFormService: TenantDomainFormService;
  let tenantDomainService: TenantDomainService;
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

    fixture = TestBed.createComponent(TenantDomainUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantDomainFormService = TestBed.inject(TenantDomainFormService);
    tenantDomainService = TestBed.inject(TenantDomainService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const tenantDomain: ITenantDomain = { id: 5642 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantDomain.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenantDomain });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tenantDomain: ITenantDomain = { id: 5642 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantDomain.tenant = tenant;

      activatedRoute.data = of({ tenantDomain });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.tenantDomain).toEqual(tenantDomain);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDomain>();
      const tenantDomain = { id: 19717 };
      vi.spyOn(tenantDomainFormService, 'getTenantDomain').mockReturnValue(tenantDomain);
      vi.spyOn(tenantDomainService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDomain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantDomain);
      saveSubject.complete();

      // THEN
      expect(tenantDomainFormService.getTenantDomain).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantDomainService.update).toHaveBeenCalledWith(expect.objectContaining(tenantDomain));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDomain>();
      const tenantDomain = { id: 19717 };
      vi.spyOn(tenantDomainFormService, 'getTenantDomain').mockReturnValue({ id: null });
      vi.spyOn(tenantDomainService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDomain: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantDomain);
      saveSubject.complete();

      // THEN
      expect(tenantDomainFormService.getTenantDomain).toHaveBeenCalled();
      expect(tenantDomainService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantDomain>();
      const tenantDomain = { id: 19717 };
      vi.spyOn(tenantDomainService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantDomain });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantDomainService.update).toHaveBeenCalled();
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
