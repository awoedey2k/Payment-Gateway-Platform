import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { JournalEntryDetail } from './journal-entry-detail';

describe('JournalEntry Management Detail Component', () => {
  let comp: JournalEntryDetail;
  let fixture: ComponentFixture<JournalEntryDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./journal-entry-detail').then(m => m.JournalEntryDetail),
              resolve: { journalEntry: () => of({ id: 28031 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(JournalEntryDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load journalEntry on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', JournalEntryDetail);

      // THEN
      expect(instance.journalEntry()).toEqual(expect.objectContaining({ id: 28031 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vi.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
