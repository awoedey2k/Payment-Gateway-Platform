import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../journal-line.test-samples';

import { JournalLineFormService } from './journal-line-form.service';

describe('JournalLine Form Service', () => {
  let service: JournalLineFormService;

  beforeEach(() => {
    service = TestBed.inject(JournalLineFormService);
  });

  describe('Service methods', () => {
    describe('createJournalLineFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJournalLineFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            debitAmount: expect.any(Object),
            creditAmount: expect.any(Object),
            account: expect.any(Object),
            journalEntry: expect.any(Object),
          }),
        );
      });

      it('passing IJournalLine should create a new form with FormGroup', () => {
        const formGroup = service.createJournalLineFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            debitAmount: expect.any(Object),
            creditAmount: expect.any(Object),
            account: expect.any(Object),
            journalEntry: expect.any(Object),
          }),
        );
      });
    });

    describe('getJournalLine', () => {
      it('should return NewJournalLine for default JournalLine initial value', () => {
        const formGroup = service.createJournalLineFormGroup(sampleWithNewData);

        const journalLine = service.getJournalLine(formGroup);

        expect(journalLine).toMatchObject(sampleWithNewData);
      });

      it('should return NewJournalLine for empty JournalLine initial value', () => {
        const formGroup = service.createJournalLineFormGroup();

        const journalLine = service.getJournalLine(formGroup);

        expect(journalLine).toMatchObject({});
      });

      it('should return IJournalLine', () => {
        const formGroup = service.createJournalLineFormGroup(sampleWithRequiredData);

        const journalLine = service.getJournalLine(formGroup);

        expect(journalLine).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJournalLine should not enable id FormControl', () => {
        const formGroup = service.createJournalLineFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJournalLine should disable id FormControl', () => {
        const formGroup = service.createJournalLineFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
