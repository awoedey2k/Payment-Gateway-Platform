import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IForexRate } from '../forex-rate.model';
import { ForexRateService } from '../service/forex-rate.service';

import { ForexRateFormService } from './forex-rate-form.service';
import { ForexRateUpdate } from './forex-rate-update';

describe('ForexRate Management Update Component', () => {
  let comp: ForexRateUpdate;
  let fixture: ComponentFixture<ForexRateUpdate>;
  let activatedRoute: ActivatedRoute;
  let forexRateFormService: ForexRateFormService;
  let forexRateService: ForexRateService;

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

    fixture = TestBed.createComponent(ForexRateUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    forexRateFormService = TestBed.inject(ForexRateFormService);
    forexRateService = TestBed.inject(ForexRateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const forexRate: IForexRate = { id: 22722 };

      activatedRoute.data = of({ forexRate });
      comp.ngOnInit();

      expect(comp.forexRate).toEqual(forexRate);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IForexRate>();
      const forexRate = { id: 29998 };
      vi.spyOn(forexRateFormService, 'getForexRate').mockReturnValue(forexRate);
      vi.spyOn(forexRateService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ forexRate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(forexRate);
      saveSubject.complete();

      // THEN
      expect(forexRateFormService.getForexRate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(forexRateService.update).toHaveBeenCalledWith(expect.objectContaining(forexRate));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IForexRate>();
      const forexRate = { id: 29998 };
      vi.spyOn(forexRateFormService, 'getForexRate').mockReturnValue({ id: null });
      vi.spyOn(forexRateService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ forexRate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(forexRate);
      saveSubject.complete();

      // THEN
      expect(forexRateFormService.getForexRate).toHaveBeenCalled();
      expect(forexRateService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IForexRate>();
      const forexRate = { id: 29998 };
      vi.spyOn(forexRateService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ forexRate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(forexRateService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
