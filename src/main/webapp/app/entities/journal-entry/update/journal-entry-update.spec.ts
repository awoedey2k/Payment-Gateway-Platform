import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IJournalEntry } from '../journal-entry.model';
import { JournalEntryService } from '../service/journal-entry.service';

import { JournalEntryFormService } from './journal-entry-form.service';
import { JournalEntryUpdate } from './journal-entry-update';

describe('JournalEntry Management Update Component', () => {
  let comp: JournalEntryUpdate;
  let fixture: ComponentFixture<JournalEntryUpdate>;
  let activatedRoute: ActivatedRoute;
  let journalEntryFormService: JournalEntryFormService;
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

    fixture = TestBed.createComponent(JournalEntryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    journalEntryFormService = TestBed.inject(JournalEntryFormService);
    journalEntryService = TestBed.inject(JournalEntryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const journalEntry: IJournalEntry = { id: 2017 };

      activatedRoute.data = of({ journalEntry });
      comp.ngOnInit();

      expect(comp.journalEntry).toEqual(journalEntry);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalEntry>();
      const journalEntry = { id: 28031 };
      vi.spyOn(journalEntryFormService, 'getJournalEntry').mockReturnValue(journalEntry);
      vi.spyOn(journalEntryService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalEntry);
      saveSubject.complete();

      // THEN
      expect(journalEntryFormService.getJournalEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(journalEntryService.update).toHaveBeenCalledWith(expect.objectContaining(journalEntry));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalEntry>();
      const journalEntry = { id: 28031 };
      vi.spyOn(journalEntryFormService, 'getJournalEntry').mockReturnValue({ id: null });
      vi.spyOn(journalEntryService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(journalEntry);
      saveSubject.complete();

      // THEN
      expect(journalEntryFormService.getJournalEntry).toHaveBeenCalled();
      expect(journalEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IJournalEntry>();
      const journalEntry = { id: 28031 };
      vi.spyOn(journalEntryService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ journalEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(journalEntryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
