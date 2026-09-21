import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDisputeEvidence } from '../dispute-evidence.model';

@Component({
  selector: 'jhi-dispute-evidence-detail',
  templateUrl: './dispute-evidence-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatetimePipe],
})
export class DisputeEvidenceDetail {
  readonly disputeEvidence = input<IDisputeEvidence | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
