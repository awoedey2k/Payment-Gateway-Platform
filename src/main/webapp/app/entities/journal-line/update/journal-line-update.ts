import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize, map } from 'rxjs';

import { IJournalEntry } from 'app/entities/journal-entry/journal-entry.model';
import { JournalEntryService } from 'app/entities/journal-entry/service/journal-entry.service';
import { ILedgerAccount } from 'app/entities/ledger-account/ledger-account.model';
import { LedgerAccountService } from 'app/entities/ledger-account/service/ledger-account.service';
import { AlertError } from 'app/shared/alert';
import { IJournalLine } from '../journal-line.model';
import { JournalLineService } from '../service/journal-line.service';

import { JournalLineFormGroup, JournalLineFormService } from './journal-line-form.service';

@Component({
  selector: 'jhi-journal-line-update',
  templateUrl: './journal-line-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class JournalLineUpdate implements OnInit {
  readonly isSaving = signal(false);
  journalLine: IJournalLine | null = null;

  ledgerAccountsSharedCollection = signal<ILedgerAccount[]>([]);
  journalEntriesSharedCollection = signal<IJournalEntry[]>([]);

  protected journalLineService = inject(JournalLineService);
  protected journalLineFormService = inject(JournalLineFormService);
  protected ledgerAccountService = inject(LedgerAccountService);
  protected journalEntryService = inject(JournalEntryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JournalLineFormGroup = this.journalLineFormService.createJournalLineFormGroup();

  compareLedgerAccount = (o1: ILedgerAccount | null, o2: ILedgerAccount | null): boolean =>
    this.ledgerAccountService.compareLedgerAccount(o1, o2);

  compareJournalEntry = (o1: IJournalEntry | null, o2: IJournalEntry | null): boolean =>
    this.journalEntryService.compareJournalEntry(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ journalLine }) => {
      this.journalLine = journalLine;
      if (journalLine) {
        this.updateForm(journalLine);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const journalLine = this.journalLineFormService.getJournalLine(this.editForm);
    if (journalLine.id === null) {
      this.subscribeToSaveResponse(this.journalLineService.create(journalLine));
    } else {
      this.subscribeToSaveResponse(this.journalLineService.update(journalLine));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IJournalLine | null>): void {
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

  protected updateForm(journalLine: IJournalLine): void {
    this.journalLine = journalLine;
    this.journalLineFormService.resetForm(this.editForm, journalLine);

    this.ledgerAccountsSharedCollection.update(ledgerAccounts =>
      this.ledgerAccountService.addLedgerAccountToCollectionIfMissing<ILedgerAccount>(ledgerAccounts, journalLine.account),
    );
    this.journalEntriesSharedCollection.update(journalEntries =>
      this.journalEntryService.addJournalEntryToCollectionIfMissing<IJournalEntry>(journalEntries, journalLine.journalEntry),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ledgerAccountService
      .query()
      .pipe(map((res: HttpResponse<ILedgerAccount[]>) => res.body ?? []))
      .pipe(
        map((ledgerAccounts: ILedgerAccount[]) =>
          this.ledgerAccountService.addLedgerAccountToCollectionIfMissing<ILedgerAccount>(ledgerAccounts, this.journalLine?.account),
        ),
      )
      .subscribe((ledgerAccounts: ILedgerAccount[]) => this.ledgerAccountsSharedCollection.set(ledgerAccounts));

    this.journalEntryService
      .query()
      .pipe(map((res: HttpResponse<IJournalEntry[]>) => res.body ?? []))
      .pipe(
        map((journalEntries: IJournalEntry[]) =>
          this.journalEntryService.addJournalEntryToCollectionIfMissing<IJournalEntry>(journalEntries, this.journalLine?.journalEntry),
        ),
      )
      .subscribe((journalEntries: IJournalEntry[]) => this.journalEntriesSharedCollection.set(journalEntries));
  }
}
