import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { DisputeEvidenceDetail } from './dispute-evidence-detail';

describe('DisputeEvidence Management Detail Component', () => {
  let comp: DisputeEvidenceDetail;
  let fixture: ComponentFixture<DisputeEvidenceDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./dispute-evidence-detail').then(m => m.DisputeEvidenceDetail),
              resolve: { disputeEvidence: () => of({ id: 6499 }) },
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
    fixture = TestBed.createComponent(DisputeEvidenceDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load disputeEvidence on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DisputeEvidenceDetail);

      // THEN
      expect(instance.disputeEvidence()).toEqual(expect.objectContaining({ id: 6499 }));
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
