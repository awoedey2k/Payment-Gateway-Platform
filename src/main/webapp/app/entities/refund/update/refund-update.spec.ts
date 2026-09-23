import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { TransactionService } from 'app/entities/transaction/service/transaction.service';
import { ITransaction } from 'app/entities/transaction/transaction.model';
import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';

import { RefundFormService } from './refund-form.service';
import { RefundUpdate } from './refund-update';

describe('Refund Management Update Component', () => {
  let comp: RefundUpdate;
  let fixture: ComponentFixture<RefundUpdate>;
  let activatedRoute: ActivatedRoute;
  let refundFormService: RefundFormService;
  let refundService: RefundService;
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

    fixture = TestBed.createComponent(RefundUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    refundFormService = TestBed.inject(RefundFormService);
    refundService = TestBed.inject(RefundService);
    transactionService = TestBed.inject(TransactionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Transaction query and add missing value', () => {
      const refund: IRefund = { id: 1065 };
      const transaction: ITransaction = { id: 29476 };
      refund.transaction = transaction;

      const transactionCollection: ITransaction[] = [{ id: 29476 }];
      vi.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const additionalTransactions = [transaction];
      const expectedCollection: ITransaction[] = [...additionalTransactions, ...transactionCollection];
      vi.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(
        transactionCollection,
        ...additionalTransactions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.transactionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const refund: IRefund = { id: 1065 };
      const transaction: ITransaction = { id: 29476 };
      refund.transaction = transaction;

      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      expect(comp.transactionsSharedCollection()).toContainEqual(transaction);
      expect(comp.refund).toEqual(refund);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRefund>();
      const refund = { id: 20042 };
      vi.spyOn(refundFormService, 'getRefund').mockReturnValue(refund);
      vi.spyOn(refundService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(refund);
      saveSubject.complete();

      // THEN
      expect(refundFormService.getRefund).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(refundService.update).toHaveBeenCalledWith(expect.objectContaining(refund));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRefund>();
      const refund = { id: 20042 };
      vi.spyOn(refundFormService, 'getRefund').mockReturnValue({ id: null });
      vi.spyOn(refundService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(refund);
      saveSubject.complete();

      // THEN
      expect(refundFormService.getRefund).toHaveBeenCalled();
      expect(refundService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRefund>();
      const refund = { id: 20042 };
      vi.spyOn(refundService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refund });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(refundService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
