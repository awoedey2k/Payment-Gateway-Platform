import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { TenantDomainDetail } from './tenant-domain-detail';

describe('TenantDomain Management Detail Component', () => {
  let comp: TenantDomainDetail;
  let fixture: ComponentFixture<TenantDomainDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./tenant-domain-detail').then(m => m.TenantDomainDetail),
              resolve: { tenantDomain: () => of({ id: 19717 }) },
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
    fixture = TestBed.createComponent(TenantDomainDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load tenantDomain on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TenantDomainDetail);

      // THEN
      expect(instance.tenantDomain()).toEqual(expect.objectContaining({ id: 19717 }));
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
