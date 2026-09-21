import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { PaymentMethodDetail } from './payment-method-detail';

describe('PaymentMethod Management Detail Component', () => {
  let comp: PaymentMethodDetail;
  let fixture: ComponentFixture<PaymentMethodDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./payment-method-detail').then(m => m.PaymentMethodDetail),
              resolve: { paymentMethod: () => of({ id: 25086 }) },
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
    fixture = TestBed.createComponent(PaymentMethodDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load paymentMethod on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PaymentMethodDetail);

      // THEN
      expect(instance.paymentMethod()).toEqual(expect.objectContaining({ id: 25086 }));
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
