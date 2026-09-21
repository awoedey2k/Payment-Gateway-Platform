import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { RoutingRuleDetail } from './routing-rule-detail';

describe('RoutingRule Management Detail Component', () => {
  let comp: RoutingRuleDetail;
  let fixture: ComponentFixture<RoutingRuleDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./routing-rule-detail').then(m => m.RoutingRuleDetail),
              resolve: { routingRule: () => of({ id: 9786 }) },
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
    fixture = TestBed.createComponent(RoutingRuleDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load routingRule on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RoutingRuleDetail);

      // THEN
      expect(instance.routingRule()).toEqual(expect.objectContaining({ id: 9786 }));
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
