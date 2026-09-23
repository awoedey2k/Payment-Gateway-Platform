import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { ILedgerAccount } from '../ledger-account.model';
import { LedgerAccountService } from '../service/ledger-account.service';

import { LedgerAccountFormService } from './ledger-account-form.service';
import { LedgerAccountUpdate } from './ledger-account-update';

describe('LedgerAccount Management Update Component', () => {
  let comp: LedgerAccountUpdate;
  let fixture: ComponentFixture<LedgerAccountUpdate>;
  let activatedRoute: ActivatedRoute;
  let ledgerAccountFormService: LedgerAccountFormService;
  let ledgerAccountService: LedgerAccountService;

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

    fixture = TestBed.createComponent(LedgerAccountUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ledgerAccountFormService = TestBed.inject(LedgerAccountFormService);
    ledgerAccountService = TestBed.inject(LedgerAccountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const ledgerAccount: ILedgerAccount = { id: 29910 };

      activatedRoute.data = of({ ledgerAccount });
      comp.ngOnInit();

      expect(comp.ledgerAccount).toEqual(ledgerAccount);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILedgerAccount>();
      const ledgerAccount = { id: 3298 };
      vi.spyOn(ledgerAccountFormService, 'getLedgerAccount').mockReturnValue(ledgerAccount);
      vi.spyOn(ledgerAccountService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ledgerAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ledgerAccount);
      saveSubject.complete();

      // THEN
      expect(ledgerAccountFormService.getLedgerAccount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ledgerAccountService.update).toHaveBeenCalledWith(expect.objectContaining(ledgerAccount));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILedgerAccount>();
      const ledgerAccount = { id: 3298 };
      vi.spyOn(ledgerAccountFormService, 'getLedgerAccount').mockReturnValue({ id: null });
      vi.spyOn(ledgerAccountService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ledgerAccount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ledgerAccount);
      saveSubject.complete();

      // THEN
      expect(ledgerAccountFormService.getLedgerAccount).toHaveBeenCalled();
      expect(ledgerAccountService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILedgerAccount>();
      const ledgerAccount = { id: 3298 };
      vi.spyOn(ledgerAccountService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ledgerAccount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ledgerAccountService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
