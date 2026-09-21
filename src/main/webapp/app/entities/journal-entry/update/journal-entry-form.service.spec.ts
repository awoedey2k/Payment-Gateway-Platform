import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../journal-entry.test-samples';

import { JournalEntryFormService } from './journal-entry-form.service';

describe('JournalEntry Form Service', () => {
  let service: JournalEntryFormService;

  beforeEach(() => {
    service = TestBed.inject(JournalEntryFormService);
  });

  describe('Service methods', () => {
    describe('createJournalEntryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJournalEntryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            description: expect.any(Object),
            postedAt: expect.any(Object),
          }),
        );
      });

      it('passing IJournalEntry should create a new form with FormGroup', () => {
        const formGroup = service.createJournalEntryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reference: expect.any(Object),
            description: expect.any(Object),
            postedAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getJournalEntry', () => {
      it('should return NewJournalEntry for default JournalEntry initial value', () => {
        const formGroup = service.createJournalEntryFormGroup(sampleWithNewData);

        const journalEntry = service.getJournalEntry(formGroup);

        expect(journalEntry).toMatchObject(sampleWithNewData);
      });

      it('should return NewJournalEntry for empty JournalEntry initial value', () => {
        const formGroup = service.createJournalEntryFormGroup();

        const journalEntry = service.getJournalEntry(formGroup);

        expect(journalEntry).toMatchObject({});
      });

      it('should return IJournalEntry', () => {
        const formGroup = service.createJournalEntryFormGroup(sampleWithRequiredData);

        const journalEntry = service.getJournalEntry(formGroup);

        expect(journalEntry).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJournalEntry should not enable id FormControl', () => {
        const formGroup = service.createJournalEntryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJournalEntry should disable id FormControl', () => {
        const formGroup = service.createJournalEntryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
