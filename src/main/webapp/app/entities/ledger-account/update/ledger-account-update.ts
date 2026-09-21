import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { LedgerAccountType } from 'app/entities/enumerations/ledger-account-type.model';
import { AlertError } from 'app/shared/alert';
import { ILedgerAccount } from '../ledger-account.model';
import { LedgerAccountService } from '../service/ledger-account.service';

import { LedgerAccountFormGroup, LedgerAccountFormService } from './ledger-account-form.service';

@Component({
  selector: 'jhi-ledger-account-update',
  templateUrl: './ledger-account-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LedgerAccountUpdate implements OnInit {
  readonly isSaving = signal(false);
  ledgerAccount: ILedgerAccount | null = null;
  ledgerAccountTypeValues = Object.keys(LedgerAccountType);

  protected ledgerAccountService = inject(LedgerAccountService);
  protected ledgerAccountFormService = inject(LedgerAccountFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LedgerAccountFormGroup = this.ledgerAccountFormService.createLedgerAccountFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ledgerAccount }) => {
      this.ledgerAccount = ledgerAccount;
      if (ledgerAccount) {
        this.updateForm(ledgerAccount);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ledgerAccount = this.ledgerAccountFormService.getLedgerAccount(this.editForm);
    if (ledgerAccount.id === null) {
      this.subscribeToSaveResponse(this.ledgerAccountService.create(ledgerAccount));
    } else {
      this.subscribeToSaveResponse(this.ledgerAccountService.update(ledgerAccount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILedgerAccount | null>): void {
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

  protected updateForm(ledgerAccount: ILedgerAccount): void {
    this.ledgerAccount = ledgerAccount;
    this.ledgerAccountFormService.resetForm(this.editForm, ledgerAccount);
  }
}
