import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IJournalLine, NewJournalLine } from '../journal-line.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJournalLine for edit and NewJournalLineFormGroupInput for create.
 */
type JournalLineFormGroupInput = IJournalLine | PartialWithRequiredKeyOf<NewJournalLine>;

type JournalLineFormDefaults = Pick<NewJournalLine, 'id'>;

type JournalLineFormGroupContent = {
  id: FormControl<IJournalLine['id'] | NewJournalLine['id']>;
  debitAmount: FormControl<IJournalLine['debitAmount']>;
  creditAmount: FormControl<IJournalLine['creditAmount']>;
  account: FormControl<IJournalLine['account']>;
  journalEntry: FormControl<IJournalLine['journalEntry']>;
};

export type JournalLineFormGroup = FormGroup<JournalLineFormGroupContent>;

@Service()
export class JournalLineFormService {
  createJournalLineFormGroup(journalLine?: JournalLineFormGroupInput): JournalLineFormGroup {
    const journalLineRawValue = {
      ...this.getFormDefaults(),
      ...(journalLine ?? { id: null }),
    };

    return new FormGroup<JournalLineFormGroupContent>({
      id: new FormControl(
        { value: journalLineRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      debitAmount: new FormControl(journalLineRawValue.debitAmount),
      creditAmount: new FormControl(journalLineRawValue.creditAmount),
      account: new FormControl(journalLineRawValue.account, {
        validators: [Validators.required],
      }),
      journalEntry: new FormControl(journalLineRawValue.journalEntry, {
        validators: [Validators.required],
      }),
    });
  }

  getJournalLine(form: JournalLineFormGroup): IJournalLine | NewJournalLine {
    return form.getRawValue();
  }

  resetForm(form: JournalLineFormGroup, journalLine: JournalLineFormGroupInput): void {
    const journalLineRawValue = { ...this.getFormDefaults(), ...journalLine };
    form.reset({
      ...journalLineRawValue,
      id: { value: journalLineRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): JournalLineFormDefaults {
    return {
      id: null,
    };
  }
}
