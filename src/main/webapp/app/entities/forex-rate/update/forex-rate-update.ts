import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert';
import { IForexRate } from '../forex-rate.model';
import { ForexRateService } from '../service/forex-rate.service';

import { ForexRateFormGroup, ForexRateFormService } from './forex-rate-form.service';

@Component({
  selector: 'jhi-forex-rate-update',
  templateUrl: './forex-rate-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ForexRateUpdate implements OnInit {
  readonly isSaving = signal(false);
  forexRate: IForexRate | null = null;

  protected forexRateService = inject(ForexRateService);
  protected forexRateFormService = inject(ForexRateFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ForexRateFormGroup = this.forexRateFormService.createForexRateFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ forexRate }) => {
      this.forexRate = forexRate;
      if (forexRate) {
        this.updateForm(forexRate);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const forexRate = this.forexRateFormService.getForexRate(this.editForm);
    if (forexRate.id === null) {
      this.subscribeToSaveResponse(this.forexRateService.create(forexRate));
    } else {
      this.subscribeToSaveResponse(this.forexRateService.update(forexRate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IForexRate | null>): void {
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

  protected updateForm(forexRate: IForexRate): void {
    this.forexRate = forexRate;
    this.forexRateFormService.resetForm(this.editForm, forexRate);
  }
}
