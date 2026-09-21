import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IJournalEntry, NewJournalEntry } from '../journal-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJournalEntry for edit and NewJournalEntryFormGroupInput for create.
 */
type JournalEntryFormGroupInput = IJournalEntry | PartialWithRequiredKeyOf<NewJournalEntry>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IJournalEntry | NewJournalEntry> = Omit<T, 'postedAt'> & {
  postedAt?: string | null;
};

type JournalEntryFormRawValue = FormValueOf<IJournalEntry>;

type NewJournalEntryFormRawValue = FormValueOf<NewJournalEntry>;

type JournalEntryFormDefaults = Pick<NewJournalEntry, 'id' | 'postedAt'>;

type JournalEntryFormGroupContent = {
  id: FormControl<JournalEntryFormRawValue['id'] | NewJournalEntry['id']>;
  reference: FormControl<JournalEntryFormRawValue['reference']>;
  description: FormControl<JournalEntryFormRawValue['description']>;
  postedAt: FormControl<JournalEntryFormRawValue['postedAt']>;
};

export type JournalEntryFormGroup = FormGroup<JournalEntryFormGroupContent>;

@Service()
export class JournalEntryFormService {
  createJournalEntryFormGroup(journalEntry?: JournalEntryFormGroupInput): JournalEntryFormGroup {
    const journalEntryRawValue = this.convertJournalEntryToJournalEntryRawValue({
      ...this.getFormDefaults(),
      ...(journalEntry ?? { id: null }),
    });

    return new FormGroup<JournalEntryFormGroupContent>({
      id: new FormControl(
        { value: journalEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reference: new FormControl(journalEntryRawValue.reference, {
        validators: [Validators.required],
      }),
      description: new FormControl(journalEntryRawValue.description, {
        validators: [Validators.required],
      }),
      postedAt: new FormControl(journalEntryRawValue.postedAt, {
        validators: [Validators.required],
      }),
    });
  }

  getJournalEntry(form: JournalEntryFormGroup): IJournalEntry | NewJournalEntry {
    return this.convertJournalEntryRawValueToJournalEntry(form.getRawValue());
  }

  resetForm(form: JournalEntryFormGroup, journalEntry: JournalEntryFormGroupInput): void {
    const journalEntryRawValue = this.convertJournalEntryToJournalEntryRawValue({ ...this.getFormDefaults(), ...journalEntry });
    form.reset({
      ...journalEntryRawValue,
      id: { value: journalEntryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): JournalEntryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      postedAt: currentTime,
    };
  }

  private convertJournalEntryRawValueToJournalEntry(
    rawJournalEntry: JournalEntryFormRawValue | NewJournalEntryFormRawValue,
  ): IJournalEntry | NewJournalEntry {
    return {
      ...rawJournalEntry,
      postedAt: dayjs(rawJournalEntry.postedAt, DATE_TIME_FORMAT),
    };
  }

  private convertJournalEntryToJournalEntryRawValue(
    journalEntry: IJournalEntry | (Partial<NewJournalEntry> & JournalEntryFormDefaults),
  ): JournalEntryFormRawValue | PartialWithRequiredKeyOf<NewJournalEntryFormRawValue> {
    return {
      ...journalEntry,
      postedAt: journalEntry.postedAt ? journalEntry.postedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
