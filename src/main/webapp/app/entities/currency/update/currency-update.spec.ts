import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ICurrency } from '../currency.model';
import { CurrencyService } from '../service/currency.service';

import { CurrencyFormService } from './currency-form.service';
import { CurrencyUpdate } from './currency-update';

describe('Currency Management Update Component', () => {
  let comp: CurrencyUpdate;
  let fixture: ComponentFixture<CurrencyUpdate>;
  let activatedRoute: ActivatedRoute;
  let currencyFormService: CurrencyFormService;
  let currencyService: CurrencyService;

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

    fixture = TestBed.createComponent(CurrencyUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    currencyFormService = TestBed.inject(CurrencyFormService);
    currencyService = TestBed.inject(CurrencyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const currency: ICurrency = { id: 21302 };

      activatedRoute.data = of({ currency });
      comp.ngOnInit();

      expect(comp.currency).toEqual(currency);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICurrency>();
      const currency = { id: 22252 };
      vi.spyOn(currencyFormService, 'getCurrency').mockReturnValue(currency);
      vi.spyOn(currencyService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currency });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(currency);
      saveSubject.complete();

      // THEN
      expect(currencyFormService.getCurrency).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(currencyService.update).toHaveBeenCalledWith(expect.objectContaining(currency));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICurrency>();
      const currency = { id: 22252 };
      vi.spyOn(currencyFormService, 'getCurrency').mockReturnValue({ id: null });
      vi.spyOn(currencyService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currency: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(currency);
      saveSubject.complete();

      // THEN
      expect(currencyFormService.getCurrency).toHaveBeenCalled();
      expect(currencyService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICurrency>();
      const currency = { id: 22252 };
      vi.spyOn(currencyService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ currency });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(currencyService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
