import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICorporateTenant } from 'app/entities/corporate-tenant/corporate-tenant.model';
import { CorporateTenantService } from 'app/entities/corporate-tenant/service/corporate-tenant.service';
import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { IAmlCheck } from '../aml-check.model';
import { AmlCheckService } from '../service/aml-check.service';

import { AmlCheckFormService } from './aml-check-form.service';
import { AmlCheckUpdate } from './aml-check-update';

describe('AmlCheck Management Update Component', () => {
  let comp: AmlCheckUpdate;
  let fixture: ComponentFixture<AmlCheckUpdate>;
  let activatedRoute: ActivatedRoute;
  let amlCheckFormService: AmlCheckFormService;
  let amlCheckService: AmlCheckService;
  let corporateTenantService: CorporateTenantService;
  let transactionService: TransactionService;

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

    fixture = TestBed.createComponent(AmlCheckUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    amlCheckFormService = TestBed.inject(AmlCheckFormService);
    amlCheckService = TestBed.inject(AmlCheckService);
    corporateTenantService = TestBed.inject(CorporateTenantService);
    transactionService = TestBed.inject(TransactionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const amlCheck: IAmlCheck = { id: 14754 };
      const tenant: ICorporateTenant = { id: 10961 };
      amlCheck.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ amlCheck });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Transaction query and add missing value', () => {
      const amlCheck: IAmlCheck = { id: 14754 };
      const transaction: ITransaction = { id: 29476 };
      amlCheck.transaction = transaction;

      const transactionCollection: ITransaction[] = [{ id: 29476 }];
      vi.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const additionalTransactions = [transaction];
      const expectedCollection: ITransaction[] = [...additionalTransactions, ...transactionCollection];
      vi.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ amlCheck });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(
        transactionCollection,
        ...additionalTransactions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.transactionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const amlCheck: IAmlCheck = { id: 14754 };
      const tenant: ICorporateTenant = { id: 10961 };
      amlCheck.tenant = tenant;
      const transaction: ITransaction = { id: 29476 };
      amlCheck.transaction = transaction;

      activatedRoute.data = of({ amlCheck });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.transactionsSharedCollection()).toContainEqual(transaction);
      expect(comp.amlCheck).toEqual(amlCheck);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAmlCheck>();
      const amlCheck = { id: 28734 };
      vi.spyOn(amlCheckFormService, 'getAmlCheck').mockReturnValue(amlCheck);
      vi.spyOn(amlCheckService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ amlCheck });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(amlCheck);
      saveSubject.complete();

      // THEN
      expect(amlCheckFormService.getAmlCheck).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(amlCheckService.update).toHaveBeenCalledWith(expect.objectContaining(amlCheck));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAmlCheck>();
      const amlCheck = { id: 28734 };
      vi.spyOn(amlCheckFormService, 'getAmlCheck').mockReturnValue({ id: null });
      vi.spyOn(amlCheckService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ amlCheck: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(amlCheck);
      saveSubject.complete();

      // THEN
      expect(amlCheckFormService.getAmlCheck).toHaveBeenCalled();
      expect(amlCheckService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAmlCheck>();
      const amlCheck = { id: 28734 };
      vi.spyOn(amlCheckService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ amlCheck });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(amlCheckService.update).toHaveBeenCalled();
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

    describe('compareTransaction', () => {
      it('should forward to transactionService', () => {
        const entity = { id: 29476 };
        const entity2 = { id: 15110 };
        vi.spyOn(transactionService, 'compareTransaction');
        comp.compareTransaction(entity, entity2);
        expect(transactionService.compareTransaction).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
