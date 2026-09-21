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
import { IDispute } from '../dispute.model';
import { DisputeService } from '../service/dispute.service';

import { DisputeFormService } from './dispute-form.service';
import { DisputeUpdate } from './dispute-update';

describe('Dispute Management Update Component', () => {
  let comp: DisputeUpdate;
  let fixture: ComponentFixture<DisputeUpdate>;
  let activatedRoute: ActivatedRoute;
  let disputeFormService: DisputeFormService;
  let disputeService: DisputeService;
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

    fixture = TestBed.createComponent(DisputeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    disputeFormService = TestBed.inject(DisputeFormService);
    disputeService = TestBed.inject(DisputeService);
    corporateTenantService = TestBed.inject(CorporateTenantService);
    transactionService = TestBed.inject(TransactionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call CorporateTenant query and add missing value', () => {
      const dispute: IDispute = { id: 5064 };
      const tenant: ICorporateTenant = { id: 10961 };
      dispute.tenant = tenant;

      const corporateTenantCollection: ICorporateTenant[] = [{ id: 10961 }];
      vi.spyOn(corporateTenantService, 'query').mockReturnValue(of(new HttpResponse({ body: corporateTenantCollection })));
      const additionalCorporateTenants = [tenant];
      const expectedCollection: ICorporateTenant[] = [...additionalCorporateTenants, ...corporateTenantCollection];
      vi.spyOn(corporateTenantService, 'addCorporateTenantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dispute });
      comp.ngOnInit();

      expect(corporateTenantService.query).toHaveBeenCalled();
      expect(corporateTenantService.addCorporateTenantToCollectionIfMissing).toHaveBeenCalledWith(
        corporateTenantCollection,
        ...additionalCorporateTenants.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.corporateTenantsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Transaction query and add missing value', () => {
      const dispute: IDispute = { id: 5064 };
      const transaction: ITransaction = { id: 29476 };
      dispute.transaction = transaction;

      const transactionCollection: ITransaction[] = [{ id: 29476 }];
      vi.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const additionalTransactions = [transaction];
      const expectedCollection: ITransaction[] = [...additionalTransactions, ...transactionCollection];
      vi.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dispute });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(
        transactionCollection,
        ...additionalTransactions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.transactionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const dispute: IDispute = { id: 5064 };
      const tenant: ICorporateTenant = { id: 10961 };
      dispute.tenant = tenant;
      const transaction: ITransaction = { id: 29476 };
      dispute.transaction = transaction;

      activatedRoute.data = of({ dispute });
      comp.ngOnInit();

      expect(comp.corporateTenantsSharedCollection()).toContainEqual(tenant);
      expect(comp.transactionsSharedCollection()).toContainEqual(transaction);
      expect(comp.dispute).toEqual(dispute);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDispute>();
      const dispute = { id: 30381 };
      vi.spyOn(disputeFormService, 'getDispute').mockReturnValue(dispute);
      vi.spyOn(disputeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(dispute);
      saveSubject.complete();

      // THEN
      expect(disputeFormService.getDispute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(disputeService.update).toHaveBeenCalledWith(expect.objectContaining(dispute));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDispute>();
      const dispute = { id: 30381 };
      vi.spyOn(disputeFormService, 'getDispute').mockReturnValue({ id: null });
      vi.spyOn(disputeService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispute: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(dispute);
      saveSubject.complete();

      // THEN
      expect(disputeFormService.getDispute).toHaveBeenCalled();
      expect(disputeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDispute>();
      const dispute = { id: 30381 };
      vi.spyOn(disputeService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(disputeService.update).toHaveBeenCalled();
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
