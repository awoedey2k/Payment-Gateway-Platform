import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IDispute } from 'app/entities/dispute/dispute.model';
import { DisputeService } from 'app/entities/dispute/service/dispute.service';
import { DisputeEvidenceType } from 'app/entities/enumerations/dispute-evidence-type.model';
import { AlertError } from 'app/shared/alert';
import { IDisputeEvidence } from '../dispute-evidence.model';
import { DisputeEvidenceService } from '../service/dispute-evidence.service';

import { DisputeEvidenceFormGroup, DisputeEvidenceFormService } from './dispute-evidence-form.service';

@Component({
  selector: 'jhi-dispute-evidence-update',
  templateUrl: './dispute-evidence-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class DisputeEvidenceUpdate implements OnInit {
  readonly isSaving = signal(false);
  disputeEvidence: IDisputeEvidence | null = null;
  disputeEvidenceTypeValues = Object.keys(DisputeEvidenceType);

  disputesSharedCollection = signal<IDispute[]>([]);

  protected disputeEvidenceService = inject(DisputeEvidenceService);
  protected disputeEvidenceFormService = inject(DisputeEvidenceFormService);
  protected disputeService = inject(DisputeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DisputeEvidenceFormGroup = this.disputeEvidenceFormService.createDisputeEvidenceFormGroup();

  compareDispute = (o1: IDispute | null, o2: IDispute | null): boolean => this.disputeService.compareDispute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ disputeEvidence }) => {
      this.disputeEvidence = disputeEvidence;
      if (disputeEvidence) {
        this.updateForm(disputeEvidence);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const disputeEvidence = this.disputeEvidenceFormService.getDisputeEvidence(this.editForm);
    if (disputeEvidence.id === null) {
      this.subscribeToSaveResponse(this.disputeEvidenceService.create(disputeEvidence));
    } else {
      this.subscribeToSaveResponse(this.disputeEvidenceService.update(disputeEvidence));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDisputeEvidence | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(disputeEvidence: IDisputeEvidence): void {
    this.disputeEvidence = disputeEvidence;
    this.disputeEvidenceFormService.resetForm(this.editForm, disputeEvidence);

    this.disputesSharedCollection.update(disputes =>
      this.disputeService.addDisputeToCollectionIfMissing<IDispute>(disputes, disputeEvidence.dispute),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.disputeService
      .query()
      .pipe(map((res: HttpResponse<IDispute[]>) => res.body ?? []))
      .pipe(
        map((disputes: IDispute[]) =>
          this.disputeService.addDisputeToCollectionIfMissing<IDispute>(disputes, this.disputeEvidence?.dispute),
        ),
      )
      .subscribe((disputes: IDispute[]) => this.disputesSharedCollection.set(disputes));
  }
}
