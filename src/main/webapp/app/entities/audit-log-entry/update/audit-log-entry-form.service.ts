import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IAuditLogEntry, NewAuditLogEntry } from '../audit-log-entry.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAuditLogEntry for edit and NewAuditLogEntryFormGroupInput for create.
 */
type AuditLogEntryFormGroupInput = IAuditLogEntry | PartialWithRequiredKeyOf<NewAuditLogEntry>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAuditLogEntry | NewAuditLogEntry> = Omit<T, 'recordedAt'> & {
  recordedAt?: string | null;
};

type AuditLogEntryFormRawValue = FormValueOf<IAuditLogEntry>;

type NewAuditLogEntryFormRawValue = FormValueOf<NewAuditLogEntry>;

type AuditLogEntryFormDefaults = Pick<NewAuditLogEntry, 'id' | 'recordedAt'>;

type AuditLogEntryFormGroupContent = {
  id: FormControl<AuditLogEntryFormRawValue['id'] | NewAuditLogEntry['id']>;
  actorType: FormControl<AuditLogEntryFormRawValue['actorType']>;
  actorId: FormControl<AuditLogEntryFormRawValue['actorId']>;
  action: FormControl<AuditLogEntryFormRawValue['action']>;
  entityType: FormControl<AuditLogEntryFormRawValue['entityType']>;
  entityId: FormControl<AuditLogEntryFormRawValue['entityId']>;
  previousHash: FormControl<AuditLogEntryFormRawValue['previousHash']>;
  entryHash: FormControl<AuditLogEntryFormRawValue['entryHash']>;
  recordedAt: FormControl<AuditLogEntryFormRawValue['recordedAt']>;
};

export type AuditLogEntryFormGroup = FormGroup<AuditLogEntryFormGroupContent>;

@Service()
export class AuditLogEntryFormService {
  createAuditLogEntryFormGroup(auditLogEntry?: AuditLogEntryFormGroupInput): AuditLogEntryFormGroup {
    const auditLogEntryRawValue = this.convertAuditLogEntryToAuditLogEntryRawValue({
      ...this.getFormDefaults(),
      ...(auditLogEntry ?? { id: null }),
    });

    return new FormGroup<AuditLogEntryFormGroupContent>({
      id: new FormControl(
        { value: auditLogEntryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      actorType: new FormControl(auditLogEntryRawValue.actorType, {
        validators: [Validators.required],
      }),
      actorId: new FormControl(auditLogEntryRawValue.actorId, {
        validators: [Validators.required],
      }),
      action: new FormControl(auditLogEntryRawValue.action, {
        validators: [Validators.required],
      }),
      entityType: new FormControl(auditLogEntryRawValue.entityType, {
        validators: [Validators.required],
      }),
      entityId: new FormControl(auditLogEntryRawValue.entityId, {
        validators: [Validators.required],
      }),
      previousHash: new FormControl(auditLogEntryRawValue.previousHash),
      entryHash: new FormControl(auditLogEntryRawValue.entryHash, {
        validators: [Validators.required],
      }),
      recordedAt: new FormControl(auditLogEntryRawValue.recordedAt, {
        validators: [Validators.required],
      }),
    });
  }

  getAuditLogEntry(form: AuditLogEntryFormGroup): IAuditLogEntry | NewAuditLogEntry {
    return this.convertAuditLogEntryRawValueToAuditLogEntry(form.getRawValue());
  }

  resetForm(form: AuditLogEntryFormGroup, auditLogEntry: AuditLogEntryFormGroupInput): void {
    const auditLogEntryRawValue = this.convertAuditLogEntryToAuditLogEntryRawValue({ ...this.getFormDefaults(), ...auditLogEntry });
    form.reset({
      ...auditLogEntryRawValue,
      id: { value: auditLogEntryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AuditLogEntryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      recordedAt: currentTime,
    };
  }

  private convertAuditLogEntryRawValueToAuditLogEntry(
    rawAuditLogEntry: AuditLogEntryFormRawValue | NewAuditLogEntryFormRawValue,
  ): IAuditLogEntry | NewAuditLogEntry {
    return {
      ...rawAuditLogEntry,
      recordedAt: dayjs(rawAuditLogEntry.recordedAt, DATE_TIME_FORMAT),
    };
  }

  private convertAuditLogEntryToAuditLogEntryRawValue(
    auditLogEntry: IAuditLogEntry | (Partial<NewAuditLogEntry> & AuditLogEntryFormDefaults),
  ): AuditLogEntryFormRawValue | PartialWithRequiredKeyOf<NewAuditLogEntryFormRawValue> {
    return {
      ...auditLogEntry,
      recordedAt: auditLogEntry.recordedAt ? auditLogEntry.recordedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
