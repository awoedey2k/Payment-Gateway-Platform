import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { ICountryPaymentMethod } from 'app/entities/country-payment-method/country-payment-method.model';
import { CountryPaymentMethodService } from 'app/entities/country-payment-method/service/country-payment-method.service';
import { TenantFeeConfigService } from '../service/tenant-fee-config.service';
import { ITenantFeeConfig } from '../tenant-fee-config.model';

import { TenantFeeConfigFormService } from './tenant-fee-config-form.service';
import { TenantFeeConfigUpdate } from './tenant-fee-config-update';

describe('TenantFeeConfig Management Update Component', () => {
  let comp: TenantFeeConfigUpdate;
  let fixture: ComponentFixture<TenantFeeConfigUpdate>;
  let activatedRoute: ActivatedRoute;
  let tenantFeeConfigFormService: TenantFeeConfigFormService;
  let tenantFeeConfigService: TenantFeeConfigService;
  let corporateTenantService: CorporateTenantService;
  let countryPaymentMethodService: CountryPaymentMethodService;

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

    fixture = TestBed.createComponent(TenantFeeConfigUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantFeeConfigFormService = TestBed.inject(TenantFeeConfigFormService);
    tenantFeeConfigService = TestBed.inject(TenantFeeConfigService);
    corporateTenantService = TestBed.inject(CorporateTenantService);
    countryPaymentMethodService = TestBed.inject(CountryPaymentMethodService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const tenantFeeConfig: ITenantFeeConfig = { id: 24087 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantFeeConfig.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenantFeeConfig });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call CountryPaymentMethod query and add missing value', () => {
      const tenantFeeConfig: ITenantFeeConfig = { id: 24087 };
      const countryPaymentMethod: ICountryPaymentMethod = { id: 19642 };
      tenantFeeConfig.countryPaymentMethod = countryPaymentMethod;

      const countryPaymentMethodCollection: ICountryPaymentMethod[] = [{ id: 19642 }];
      vi.spyOn(countryPaymentMethodService, 'query').mockReturnValue(of(new HttpResponse({ body: countryPaymentMethodCollection })));
      const additionalCountryPaymentMethods = [countryPaymentMethod];
      const expectedCollection: ICountryPaymentMethod[] = [...additionalCountryPaymentMethods, ...countryPaymentMethodCollection];
      vi.spyOn(countryPaymentMethodService, 'addCountryPaymentMethodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenantFeeConfig });
      comp.ngOnInit();

      expect(countryPaymentMethodService.query).toHaveBeenCalled();
      expect(countryPaymentMethodService.addCountryPaymentMethodToCollectionIfMissing).toHaveBeenCalledWith(
        countryPaymentMethodCollection,
        ...additionalCountryPaymentMethods.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.countryPaymentMethodsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tenantFeeConfig: ITenantFeeConfig = { id: 24087 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantFeeConfig.tenant = tenant;
      const countryPaymentMethod: ICountryPaymentMethod = { id: 19642 };
      tenantFeeConfig.countryPaymentMethod = countryPaymentMethod;

      activatedRoute.data = of({ tenantFeeConfig });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.countryPaymentMethodsSharedCollection()).toContainEqual(countryPaymentMethod);
      expect(comp.tenantFeeConfig).toEqual(tenantFeeConfig);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantFeeConfig>();
      const tenantFeeConfig = { id: 22814 };
      vi.spyOn(tenantFeeConfigFormService, 'getTenantFeeConfig').mockReturnValue(tenantFeeConfig);
      vi.spyOn(tenantFeeConfigService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantFeeConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantFeeConfig);
      saveSubject.complete();

      // THEN
      expect(tenantFeeConfigFormService.getTenantFeeConfig).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantFeeConfigService.update).toHaveBeenCalledWith(expect.objectContaining(tenantFeeConfig));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantFeeConfig>();
      const tenantFeeConfig = { id: 22814 };
      vi.spyOn(tenantFeeConfigFormService, 'getTenantFeeConfig').mockReturnValue({ id: null });
      vi.spyOn(tenantFeeConfigService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantFeeConfig: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantFeeConfig);
      saveSubject.complete();

      // THEN
      expect(tenantFeeConfigFormService.getTenantFeeConfig).toHaveBeenCalled();
      expect(tenantFeeConfigService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantFeeConfig>();
      const tenantFeeConfig = { id: 22814 };
      vi.spyOn(tenantFeeConfigService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantFeeConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantFeeConfigService.update).toHaveBeenCalled();
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

    describe('compareCountryPaymentMethod', () => {
      it('should forward to countryPaymentMethodService', () => {
        const entity = { id: 19642 };
        const entity2 = { id: 27921 };
        vi.spyOn(countryPaymentMethodService, 'compareCountryPaymentMethod');
        comp.compareCountryPaymentMethod(entity, entity2);
        expect(countryPaymentMethodService.compareCountryPaymentMethod).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
