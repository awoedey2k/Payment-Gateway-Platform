import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IPaymentMethod } from '../payment-method.model';
import { PaymentMethodService } from '../service/payment-method.service';

import { PaymentMethodFormService } from './payment-method-form.service';
import { PaymentMethodUpdate } from './payment-method-update';

describe('PaymentMethod Management Update Component', () => {
  let comp: PaymentMethodUpdate;
  let fixture: ComponentFixture<PaymentMethodUpdate>;
  let activatedRoute: ActivatedRoute;
  let paymentMethodFormService: PaymentMethodFormService;
  let paymentMethodService: PaymentMethodService;

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

    fixture = TestBed.createComponent(PaymentMethodUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paymentMethodFormService = TestBed.inject(PaymentMethodFormService);
    paymentMethodService = TestBed.inject(PaymentMethodService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const paymentMethod: IPaymentMethod = { id: 19925 };

      activatedRoute.data = of({ paymentMethod });
      comp.ngOnInit();

      expect(comp.paymentMethod).toEqual(paymentMethod);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaymentMethod>();
      const paymentMethod = { id: 25086 };
      vi.spyOn(paymentMethodFormService, 'getPaymentMethod').mockReturnValue(paymentMethod);
      vi.spyOn(paymentMethodService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paymentMethod);
      saveSubject.complete();

      // THEN
      expect(paymentMethodFormService.getPaymentMethod).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paymentMethodService.update).toHaveBeenCalledWith(expect.objectContaining(paymentMethod));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaymentMethod>();
      const paymentMethod = { id: 25086 };
      vi.spyOn(paymentMethodFormService, 'getPaymentMethod').mockReturnValue({ id: null });
      vi.spyOn(paymentMethodService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethod: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paymentMethod);
      saveSubject.complete();

      // THEN
      expect(paymentMethodFormService.getPaymentMethod).toHaveBeenCalled();
      expect(paymentMethodService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPaymentMethod>();
      const paymentMethod = { id: 25086 };
      vi.spyOn(paymentMethodService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paymentMethodService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
