import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { IApiKey } from '../api-key.model';
import { ApiKeyService } from '../service/api-key.service';

import { ApiKeyFormService } from './api-key-form.service';
import { ApiKeyUpdate } from './api-key-update';

describe('ApiKey Management Update Component', () => {
  let comp: ApiKeyUpdate;
  let fixture: ComponentFixture<ApiKeyUpdate>;
  let activatedRoute: ActivatedRoute;
  let apiKeyFormService: ApiKeyFormService;
  let apiKeyService: ApiKeyService;
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

    fixture = TestBed.createComponent(ApiKeyUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    apiKeyFormService = TestBed.inject(ApiKeyFormService);
    apiKeyService = TestBed.inject(ApiKeyService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const apiKey: IApiKey = { id: 24872 };
      const tenant: ICorporateTenant = { id: 10961 };
      apiKey.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ apiKey });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const apiKey: IApiKey = { id: 24872 };
      const tenant: ICorporateTenant = { id: 10961 };
      apiKey.tenant = tenant;

      activatedRoute.data = of({ apiKey });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.apiKey).toEqual(apiKey);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IApiKey>();
      const apiKey = { id: 16763 };
      vi.spyOn(apiKeyFormService, 'getApiKey').mockReturnValue(apiKey);
      vi.spyOn(apiKeyService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ apiKey });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(apiKey);
      saveSubject.complete();

      // THEN
      expect(apiKeyFormService.getApiKey).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(apiKeyService.update).toHaveBeenCalledWith(expect.objectContaining(apiKey));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IApiKey>();
      const apiKey = { id: 16763 };
      vi.spyOn(apiKeyFormService, 'getApiKey').mockReturnValue({ id: null });
      vi.spyOn(apiKeyService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ apiKey: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(apiKey);
      saveSubject.complete();

      // THEN
      expect(apiKeyFormService.getApiKey).toHaveBeenCalled();
      expect(apiKeyService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IApiKey>();
      const apiKey = { id: 16763 };
      vi.spyOn(apiKeyService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ apiKey });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(apiKeyService.update).toHaveBeenCalled();
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
