import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { CorporateTenantDetail } from './corporate-tenant-detail';

describe('CorporateTenant Management Detail Component', () => {
  let comp: CorporateTenantDetail;
  let fixture: ComponentFixture<CorporateTenantDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./corporate-tenant-detail').then(m => m.CorporateTenantDetail),
              resolve: { corporateTenant: () => of({ id: 10961 }) },
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
    fixture = TestBed.createComponent(CorporateTenantDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load corporateTenant on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CorporateTenantDetail);

      // THEN
      expect(instance.corporateTenant()).toEqual(expect.objectContaining({ id: 10961 }));
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
