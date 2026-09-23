import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { WebhookDeliveryAttemptDetail } from './webhook-delivery-attempt-detail';

describe('WebhookDeliveryAttempt Management Detail Component', () => {
  let comp: WebhookDeliveryAttemptDetail;
  let fixture: ComponentFixture<WebhookDeliveryAttemptDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./webhook-delivery-attempt-detail').then(m => m.WebhookDeliveryAttemptDetail),
              resolve: { webhookDeliveryAttempt: () => of({ id: 3237 }) },
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
    fixture = TestBed.createComponent(WebhookDeliveryAttemptDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load webhookDeliveryAttempt on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WebhookDeliveryAttemptDetail);

      // THEN
      expect(instance.webhookDeliveryAttempt()).toEqual(expect.objectContaining({ id: 3237 }));
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
