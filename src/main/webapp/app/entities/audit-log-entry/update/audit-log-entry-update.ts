import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { ActorType } from 'app/entities/enumerations/actor-type.model';
import { AlertError } from 'app/shared/alert';
import { IAuditLogEntry } from '../audit-log-entry.model';
import { AuditLogEntryService } from '../service/audit-log-entry.service';

import { AuditLogEntryFormGroup, AuditLogEntryFormService } from './audit-log-entry-form.service';

@Component({
  selector: 'jhi-audit-log-entry-update',
  templateUrl: './audit-log-entry-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AuditLogEntryUpdate implements OnInit {
  readonly isSaving = signal(false);
  auditLogEntry: IAuditLogEntry | null = null;
  actorTypeValues = Object.keys(ActorType);

  protected auditLogEntryService = inject(AuditLogEntryService);
  protected auditLogEntryFormService = inject(AuditLogEntryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AuditLogEntryFormGroup = this.auditLogEntryFormService.createAuditLogEntryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ auditLogEntry }) => {
      this.auditLogEntry = auditLogEntry;
      if (auditLogEntry) {
        this.updateForm(auditLogEntry);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const auditLogEntry = this.auditLogEntryFormService.getAuditLogEntry(this.editForm);
    if (auditLogEntry.id === null) {
      this.subscribeToSaveResponse(this.auditLogEntryService.create(auditLogEntry));
    } else {
      this.subscribeToSaveResponse(this.auditLogEntryService.update(auditLogEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAuditLogEntry | null>): void {
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

  protected updateForm(auditLogEntry: IAuditLogEntry): void {
    this.auditLogEntry = auditLogEntry;
    this.auditLogEntryFormService.resetForm(this.editForm, auditLogEntry);
  }
}
