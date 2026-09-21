import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IJournalEntry } from 'app/entities/journal-entry/journal-entry.model';
import { JournalEntryService } from 'app/entities/journal-entry/service/journal-entry.service';
import { ILedgerAccount } from 'app/entities/ledger-account/ledger-account.model';
import { LedgerAccountService } from 'app/entities/ledger-account/service/ledger-account.service';
import { IJournalLine } from '../journal-line.model';
import { JournalLineService } from '../service/journal-line.service';

import { JournalLineFormService } from './journal-line-form.service';
import { JournalLineUpdate } from './journal-line-update';

describe('JournalLine Management Update Component', () => {
  let comp: JournalLineUpdate;
  let fixture: ComponentFixture<JournalLineUpdate>;
  let activatedRoute: ActivatedRoute;
  let journalLineFormService: JournalLineFormService;
  let journalLineService: JournalLineService;
  let ledgerAccountService: LedgerAccountService;
  let journalEntryService: JournalEntryService;

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

    fixture = TestBed.createComponent(JournalLineUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    journalLineFormService = TestBed.inject(JournalLineFormService);
    journalLineService = TestBed.inject(JournalLineService);
    ledgerAccountService = TestBed.inject(LedgerAccountService);
    journalEntryService = TestBed.inject(JournalEntryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call LedgerAccount query and add missing value', () => {
      const journalLine: IJournalLine = { id: 32513 };
      const account: ILedgerAccount = { id: 3298 };
      journalLine.account = account;

      const ledgerAccountCollection: ILedgerAccount[] = [{ id: 3298 }];
      vi.spyOn(ledgerAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: ledgerAccountCollection })));
      const additionalLedgerAccounts = [account];
      const expectedCollection: ILedgerAccount[] = [...additionalLedgerAccounts, ...ledgerAccountCollection];
      vi.spyOn(ledgerAccountService, 'addLedgerAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ journalLine });
      comp.ngOnInit();

      expect(ledgerAccountService.query).toHaveBeenCalled();
      expect(ledgerAccountService.addLedgerAccountToCollectionIfMissing).toHaveBeenCalledWith(
        ledgerAccountCollection,
        ...additionalLedgerAccounts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ledgerAccountsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call JournalEntry query and add missing value', () => {
      const journalLine: IJournalLine = { id: 32513 };
      const journalEntry: IJournalEntry = { id: 28031 };
      journalLine.journalEntry = journalEntry;

      const journalEntryCollection: IJournalEntry[] = [{ id: 28031 }];
      vi.spyOn(journalEntryService, 'query').mockReturnValue(of(new HttpResponse({ body: journalEntryCollection })));
      const additionalJournalEntries = [journalEntry];
      const expectedCollection: IJournalEntry[] = [...additionalJournalEntries, ...journalEntryCollection];
      vi.spyOn(journalEntryService, 'addJournalEntryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ journalLine });
      comp.ngOnInit();

      expect(journalEntryService.query).toHaveBeenCalled();
      expect(journalEntryService.addJournalEntryToCollectionIfMissing).toHaveBeenCalledWith(
        journalEntryCollection,
        ...additionalJournalEntries.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.journalEntriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const journalLine: IJournalLine = { id: 32513 };
      const account: ILedgerAccount = { id: 3298 };
      journalLine.account = account;
      const journalEntry: IJournalEntry = { id: 28031 };
      journalLine.journalEntry = journalEntry;

      activatedRoute.data = of({ journalLine });
      comp.ngOnInit();

      expect(comp.ledgerAccountsSharedCollection()).toContainEqual(account);
      expect(comp.journalEntriesSharedCollection()).toContainEqual(journalEntry);
      expect(comp.journalLine).toEqual(journalLine);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalLine>();
      const journalLine = { id: 13943 };
      vi.spyOn(journalLineFormService, 'getJournalLine').mockReturnValue(journalLine);
      vi.spyOn(journalLineService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalLine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalLine);
      saveSubject.complete();

      // THEN
      expect(journalLineFormService.getJournalLine).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(journalLineService.update).toHaveBeenCalledWith(expect.objectContaining(journalLine));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalLine>();
      const journalLine = { id: 13943 };
      vi.spyOn(journalLineFormService, 'getJournalLine').mockReturnValue({ id: null });
      vi.spyOn(journalLineService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalLine: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalLine);
      saveSubject.complete();

      // THEN
      expect(journalLineFormService.getJournalLine).toHaveBeenCalled();
      expect(journalLineService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalLine>();
      const journalLine = { id: 13943 };
      vi.spyOn(journalLineService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalLine });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(journalLineService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLedgerAccount', () => {
      it('should forward to ledgerAccountService', () => {
        const entity = { id: 3298 };
        const entity2 = { id: 29910 };
        vi.spyOn(ledgerAccountService, 'compareLedgerAccount');
        comp.compareLedgerAccount(entity, entity2);
        expect(ledgerAccountService.compareLedgerAccount).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJournalEntry', () => {
      it('should forward to journalEntryService', () => {
        const entity = { id: 28031 };
        const entity2 = { id: 2017 };
        vi.spyOn(journalEntryService, 'compareJournalEntry');
        comp.compareJournalEntry(entity, entity2);
        expect(journalEntryService.compareJournalEntry).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
