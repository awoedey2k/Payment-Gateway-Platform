import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { TenantFeeConfigDetail } from './tenant-fee-config-detail';

describe('TenantFeeConfig Management Detail Component', () => {
  let comp: TenantFeeConfigDetail;
  let fixture: ComponentFixture<TenantFeeConfigDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./tenant-fee-config-detail').then(m => m.TenantFeeConfigDetail),
              resolve: { tenantFeeConfig: () => of({ id: 22814 }) },
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
    fixture = TestBed.createComponent(TenantFeeConfigDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load tenantFeeConfig on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TenantFeeConfigDetail);

      // THEN
      expect(instance.tenantFeeConfig()).toEqual(expect.objectContaining({ id: 22814 }));
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
