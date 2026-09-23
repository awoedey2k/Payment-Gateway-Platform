import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { IPaymentMethod } from 'app/entities/payment-method/payment-method.model';
import { PaymentMethodService } from 'app/entities/payment-method/service/payment-method.service';
import { ICountryPaymentMethod } from '../country-payment-method.model';
import { CountryPaymentMethodService } from '../service/country-payment-method.service';

import { CountryPaymentMethodFormService } from './country-payment-method-form.service';
import { CountryPaymentMethodUpdate } from './country-payment-method-update';

describe('CountryPaymentMethod Management Update Component', () => {
  let comp: CountryPaymentMethodUpdate;
  let fixture: ComponentFixture<CountryPaymentMethodUpdate>;
  let activatedRoute: ActivatedRoute;
  let countryPaymentMethodFormService: CountryPaymentMethodFormService;
  let countryPaymentMethodService: CountryPaymentMethodService;
  let countryService: CountryService;
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

    fixture = TestBed.createComponent(CountryPaymentMethodUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    countryPaymentMethodFormService = TestBed.inject(CountryPaymentMethodFormService);
    countryPaymentMethodService = TestBed.inject(CountryPaymentMethodService);
    countryService = TestBed.inject(CountryService);
    paymentMethodService = TestBed.inject(PaymentMethodService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Country query and add missing value', () => {
      const countryPaymentMethod: ICountryPaymentMethod = { id: 27921 };
      const country: ICountry = { id: 21165 };
      countryPaymentMethod.country = country;

      const countryCollection: ICountry[] = [{ id: 21165 }];
      vi.spyOn(countryService, 'query').mockReturnValue(of(new HttpResponse({ body: countryCollection })));
      const additionalCountries = [country];
      const expectedCollection: ICountry[] = [...additionalCountries, ...countryCollection];
      vi.spyOn(countryService, 'addCountryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ countryPaymentMethod });
      comp.ngOnInit();

      expect(countryService.query).toHaveBeenCalled();
      expect(countryService.addCountryToCollectionIfMissing).toHaveBeenCalledWith(
        countryCollection,
        ...additionalCountries.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.countriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PaymentMethod query and add missing value', () => {
      const countryPaymentMethod: ICountryPaymentMethod = { id: 27921 };
      const paymentMethod: IPaymentMethod = { id: 25086 };
      countryPaymentMethod.paymentMethod = paymentMethod;

      const paymentMethodCollection: IPaymentMethod[] = [{ id: 25086 }];
      vi.spyOn(paymentMethodService, 'query').mockReturnValue(of(new HttpResponse({ body: paymentMethodCollection })));
      const additionalPaymentMethods = [paymentMethod];
      const expectedCollection: IPaymentMethod[] = [...additionalPaymentMethods, ...paymentMethodCollection];
      vi.spyOn(paymentMethodService, 'addPaymentMethodToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ countryPaymentMethod });
      comp.ngOnInit();

      expect(paymentMethodService.query).toHaveBeenCalled();
      expect(paymentMethodService.addPaymentMethodToCollectionIfMissing).toHaveBeenCalledWith(
        paymentMethodCollection,
        ...additionalPaymentMethods.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.paymentMethodsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const countryPaymentMethod: ICountryPaymentMethod = { id: 27921 };
      const country: ICountry = { id: 21165 };
      countryPaymentMethod.country = country;
      const paymentMethod: IPaymentMethod = { id: 25086 };
      countryPaymentMethod.paymentMethod = paymentMethod;

      activatedRoute.data = of({ countryPaymentMethod });
      comp.ngOnInit();

      expect(comp.countriesSharedCollection()).toContainEqual(country);
      expect(comp.paymentMethodsSharedCollection()).toContainEqual(paymentMethod);
      expect(comp.countryPaymentMethod).toEqual(countryPaymentMethod);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICountryPaymentMethod>();
      const countryPaymentMethod = { id: 19642 };
      vi.spyOn(countryPaymentMethodFormService, 'getCountryPaymentMethod').mockReturnValue(countryPaymentMethod);
      vi.spyOn(countryPaymentMethodService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ countryPaymentMethod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(countryPaymentMethod);
      saveSubject.complete();

      // THEN
      expect(countryPaymentMethodFormService.getCountryPaymentMethod).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(countryPaymentMethodService.update).toHaveBeenCalledWith(expect.objectContaining(countryPaymentMethod));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICountryPaymentMethod>();
      const countryPaymentMethod = { id: 19642 };
      vi.spyOn(countryPaymentMethodFormService, 'getCountryPaymentMethod').mockReturnValue({ id: null });
      vi.spyOn(countryPaymentMethodService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ countryPaymentMethod: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(countryPaymentMethod);
      saveSubject.complete();

      // THEN
      expect(countryPaymentMethodFormService.getCountryPaymentMethod).toHaveBeenCalled();
      expect(countryPaymentMethodService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICountryPaymentMethod>();
      const countryPaymentMethod = { id: 19642 };
      vi.spyOn(countryPaymentMethodService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ countryPaymentMethod });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(countryPaymentMethodService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCountry', () => {
      it('should forward to countryService', () => {
        const entity = { id: 21165 };
        const entity2 = { id: 2258 };
        vi.spyOn(countryService, 'compareCountry');
        comp.compareCountry(entity, entity2);
        expect(countryService.compareCountry).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePaymentMethod', () => {
      it('should forward to paymentMethodService', () => {
        const entity = { id: 25086 };
        const entity2 = { id: 19925 };
        vi.spyOn(paymentMethodService, 'comparePaymentMethod');
        comp.comparePaymentMethod(entity, entity2);
        expect(paymentMethodService.comparePaymentMethod).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
