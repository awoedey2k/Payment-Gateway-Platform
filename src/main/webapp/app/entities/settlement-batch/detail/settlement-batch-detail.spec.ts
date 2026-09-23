import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { SettlementBatchDetail } from './settlement-batch-detail';

describe('SettlementBatch Management Detail Component', () => {
  let comp: SettlementBatchDetail;
  let fixture: ComponentFixture<SettlementBatchDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./settlement-batch-detail').then(m => m.SettlementBatchDetail),
              resolve: { settlementBatch: () => of({ id: 6598 }) },
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
    fixture = TestBed.createComponent(SettlementBatchDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load settlementBatch on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SettlementBatchDetail);

      // THEN
      expect(instance.settlementBatch()).toEqual(expect.objectContaining({ id: 6598 }));
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
