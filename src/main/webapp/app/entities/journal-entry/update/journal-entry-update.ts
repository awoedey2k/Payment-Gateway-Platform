import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { IJournalEntry } from '../journal-entry.model';
import { JournalEntryService } from '../service/journal-entry.service';

import { JournalEntryFormGroup, JournalEntryFormService } from './journal-entry-form.service';

@Component({
  selector: 'jhi-journal-entry-update',
  templateUrl: './journal-entry-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class JournalEntryUpdate implements OnInit {
  readonly isSaving = signal(false);
  journalEntry: IJournalEntry | null = null;

  protected journalEntryService = inject(JournalEntryService);
  protected journalEntryFormService = inject(JournalEntryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JournalEntryFormGroup = this.journalEntryFormService.createJournalEntryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ journalEntry }) => {
      this.journalEntry = journalEntry;
      if (journalEntry) {
        this.updateForm(journalEntry);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const journalEntry = this.journalEntryFormService.getJournalEntry(this.editForm);
    if (journalEntry.id === null) {
      this.subscribeToSaveResponse(this.journalEntryService.create(journalEntry));
    } else {
      this.subscribeToSaveResponse(this.journalEntryService.update(journalEntry));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IJournalEntry | null>): void {
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

  protected updateForm(journalEntry: IJournalEntry): void {
    this.journalEntry = journalEntry;
    this.journalEntryFormService.resetForm(this.editForm, journalEntry);
  }
}
