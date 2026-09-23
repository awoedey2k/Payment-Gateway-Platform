import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { PayoutScheduleDetail } from './payout-schedule-detail';

describe('PayoutSchedule Management Detail Component', () => {
  let comp: PayoutScheduleDetail;
  let fixture: ComponentFixture<PayoutScheduleDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./payout-schedule-detail').then(m => m.PayoutScheduleDetail),
              resolve: { payoutSchedule: () => of({ id: 4713 }) },
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
    fixture = TestBed.createComponent(PayoutScheduleDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load payoutSchedule on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PayoutScheduleDetail);

      // THEN
      expect(instance.payoutSchedule()).toEqual(expect.objectContaining({ id: 4713 }));
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
