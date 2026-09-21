import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config';
import { IDisputeEvidence, NewDisputeEvidence } from '../dispute-evidence.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDisputeEvidence for edit and NewDisputeEvidenceFormGroupInput for create.
 */
type DisputeEvidenceFormGroupInput = IDisputeEvidence | PartialWithRequiredKeyOf<NewDisputeEvidence>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDisputeEvidence | NewDisputeEvidence> = Omit<T, 'uploadedAt'> & {
  uploadedAt?: string | null;
};

type DisputeEvidenceFormRawValue = FormValueOf<IDisputeEvidence>;

type NewDisputeEvidenceFormRawValue = FormValueOf<NewDisputeEvidence>;

type DisputeEvidenceFormDefaults = Pick<NewDisputeEvidence, 'id' | 'uploadedAt'>;

type DisputeEvidenceFormGroupContent = {
  id: FormControl<DisputeEvidenceFormRawValue['id'] | NewDisputeEvidence['id']>;
  evidenceType: FormControl<DisputeEvidenceFormRawValue['evidenceType']>;
  fileName: FormControl<DisputeEvidenceFormRawValue['fileName']>;
  fileUrl: FormControl<DisputeEvidenceFormRawValue['fileUrl']>;
  fileSizeBytes: FormControl<DisputeEvidenceFormRawValue['fileSizeBytes']>;
  mimeType: FormControl<DisputeEvidenceFormRawValue['mimeType']>;
  sha256Checksum: FormControl<DisputeEvidenceFormRawValue['sha256Checksum']>;
  uploadedAt: FormControl<DisputeEvidenceFormRawValue['uploadedAt']>;
  dispute: FormControl<DisputeEvidenceFormRawValue['dispute']>;
};

export type DisputeEvidenceFormGroup = FormGroup<DisputeEvidenceFormGroupContent>;

@Service()
export class DisputeEvidenceFormService {
  createDisputeEvidenceFormGroup(disputeEvidence?: DisputeEvidenceFormGroupInput): DisputeEvidenceFormGroup {
    const disputeEvidenceRawValue = this.convertDisputeEvidenceToDisputeEvidenceRawValue({
      ...this.getFormDefaults(),
      ...(disputeEvidence ?? { id: null }),
    });

    return new FormGroup<DisputeEvidenceFormGroupContent>({
      id: new FormControl(
        { value: disputeEvidenceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      evidenceType: new FormControl(disputeEvidenceRawValue.evidenceType, {
        validators: [Validators.required],
      }),
      fileName: new FormControl(disputeEvidenceRawValue.fileName, {
        validators: [Validators.required],
      }),
      fileUrl: new FormControl(disputeEvidenceRawValue.fileUrl, {
        validators: [Validators.required],
      }),
      fileSizeBytes: new FormControl(disputeEvidenceRawValue.fileSizeBytes, {
        validators: [Validators.required],
      }),
      mimeType: new FormControl(disputeEvidenceRawValue.mimeType, {
        validators: [Validators.required],
      }),
      sha256Checksum: new FormControl(disputeEvidenceRawValue.sha256Checksum, {
        validators: [Validators.required],
      }),
      uploadedAt: new FormControl(disputeEvidenceRawValue.uploadedAt, {
        validators: [Validators.required],
      }),
      dispute: new FormControl(disputeEvidenceRawValue.dispute, {
        validators: [Validators.required],
      }),
    });
  }

  getDisputeEvidence(form: DisputeEvidenceFormGroup): IDisputeEvidence | NewDisputeEvidence {
    return this.convertDisputeEvidenceRawValueToDisputeEvidence(form.getRawValue());
  }

  resetForm(form: DisputeEvidenceFormGroup, disputeEvidence: DisputeEvidenceFormGroupInput): void {
    const disputeEvidenceRawValue = this.convertDisputeEvidenceToDisputeEvidenceRawValue({ ...this.getFormDefaults(), ...disputeEvidence });
    form.reset({
      ...disputeEvidenceRawValue,
      id: { value: disputeEvidenceRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DisputeEvidenceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      uploadedAt: currentTime,
    };
  }

  private convertDisputeEvidenceRawValueToDisputeEvidence(
    rawDisputeEvidence: DisputeEvidenceFormRawValue | NewDisputeEvidenceFormRawValue,
  ): IDisputeEvidence | NewDisputeEvidence {
    return {
      ...rawDisputeEvidence,
      uploadedAt: dayjs(rawDisputeEvidence.uploadedAt, DATE_TIME_FORMAT),
    };
  }

  private convertDisputeEvidenceToDisputeEvidenceRawValue(
    disputeEvidence: IDisputeEvidence | (Partial<NewDisputeEvidence> & DisputeEvidenceFormDefaults),
  ): DisputeEvidenceFormRawValue | PartialWithRequiredKeyOf<NewDisputeEvidenceFormRawValue> {
    return {
      ...disputeEvidence,
      uploadedAt: disputeEvidence.uploadedAt ? disputeEvidence.uploadedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
