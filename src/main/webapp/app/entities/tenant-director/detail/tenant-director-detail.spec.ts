import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { TenantDirectorDetail } from './tenant-director-detail';

describe('TenantDirector Management Detail Component', () => {
  let comp: TenantDirectorDetail;
  let fixture: ComponentFixture<TenantDirectorDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./tenant-director-detail').then(m => m.TenantDirectorDetail),
              resolve: { tenantDirector: () => of({ id: 7225 }) },
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
    fixture = TestBed.createComponent(TenantDirectorDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load tenantDirector on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TenantDirectorDetail);

      // THEN
      expect(instance.tenantDirector()).toEqual(expect.objectContaining({ id: 7225 }));
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
