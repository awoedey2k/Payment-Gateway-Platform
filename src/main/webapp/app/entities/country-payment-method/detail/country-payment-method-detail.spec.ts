import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { CountryPaymentMethodDetail } from './country-payment-method-detail';

describe('CountryPaymentMethod Management Detail Component', () => {
  let comp: CountryPaymentMethodDetail;
  let fixture: ComponentFixture<CountryPaymentMethodDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./country-payment-method-detail').then(m => m.CountryPaymentMethodDetail),
              resolve: { countryPaymentMethod: () => of({ id: 19642 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CountryPaymentMethodDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load countryPaymentMethod on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CountryPaymentMethodDetail);

      // THEN
      expect(instance.countryPaymentMethod()).toEqual(expect.objectContaining({ id: 19642 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vi.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
