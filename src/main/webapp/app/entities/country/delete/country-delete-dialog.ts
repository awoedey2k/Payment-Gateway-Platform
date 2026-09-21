import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config';
import { AlertError } from 'app/shared/alert';
import { ICountry } from '../country.model';
import { CountryService } from '../service/country.service';

@Component({
  templateUrl: './country-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class CountryDeleteDialog {
  country?: ICountry;

  protected readonly countryService = inject(CountryService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.countryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
