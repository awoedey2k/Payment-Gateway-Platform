import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { WebhookSubscriptionDetail } from './webhook-subscription-detail';

describe('WebhookSubscription Management Detail Component', () => {
  let comp: WebhookSubscriptionDetail;
  let fixture: ComponentFixture<WebhookSubscriptionDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./webhook-subscription-detail').then(m => m.WebhookSubscriptionDetail),
              resolve: { webhookSubscription: () => of({ id: 28283 }) },
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
    fixture = TestBed.createComponent(WebhookSubscriptionDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load webhookSubscription on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WebhookSubscriptionDetail);

      // THEN
      expect(instance.webhookSubscription()).toEqual(expect.objectContaining({ id: 28283 }));
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
