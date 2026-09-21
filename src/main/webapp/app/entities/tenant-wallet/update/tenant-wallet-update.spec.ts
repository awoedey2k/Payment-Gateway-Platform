import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { TenantWalletService } from '../service/tenant-wallet.service';
import { ITenantWallet } from '../tenant-wallet.model';

import { TenantWalletFormService } from './tenant-wallet-form.service';
import { TenantWalletUpdate } from './tenant-wallet-update';

describe('TenantWallet Management Update Component', () => {
  let comp: TenantWalletUpdate;
  let fixture: ComponentFixture<TenantWalletUpdate>;
  let activatedRoute: ActivatedRoute;
  let tenantWalletFormService: TenantWalletFormService;
  let tenantWalletService: TenantWalletService;
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

    fixture = TestBed.createComponent(TenantWalletUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tenantWalletFormService = TestBed.inject(TenantWalletFormService);
    tenantWalletService = TestBed.inject(TenantWalletService);
    corporateTenantService = TestBed.inject(CorporateTenantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const tenantWallet: ITenantWallet = { id: 21354 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantWallet.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tenantWallet });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const tenantWallet: ITenantWallet = { id: 21354 };
      const tenant: ICorporateTenant = { id: 10961 };
      tenantWallet.tenant = tenant;

      activatedRoute.data = of({ tenantWallet });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.tenantWallet).toEqual(tenantWallet);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantWallet>();
      const tenantWallet = { id: 17782 };
      vi.spyOn(tenantWalletFormService, 'getTenantWallet').mockReturnValue(tenantWallet);
      vi.spyOn(tenantWalletService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantWallet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantWallet);
      saveSubject.complete();

      // THEN
      expect(tenantWalletFormService.getTenantWallet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tenantWalletService.update).toHaveBeenCalledWith(expect.objectContaining(tenantWallet));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantWallet>();
      const tenantWallet = { id: 17782 };
      vi.spyOn(tenantWalletFormService, 'getTenantWallet').mockReturnValue({ id: null });
      vi.spyOn(tenantWalletService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantWallet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(tenantWallet);
      saveSubject.complete();

      // THEN
      expect(tenantWalletFormService.getTenantWallet).toHaveBeenCalled();
      expect(tenantWalletService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITenantWallet>();
      const tenantWallet = { id: 17782 };
      vi.spyOn(tenantWalletService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tenantWallet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tenantWalletService.update).toHaveBeenCalled();
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
