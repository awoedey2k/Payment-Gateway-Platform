import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { AuditLogEntryDetail } from './audit-log-entry-detail';

describe('AuditLogEntry Management Detail Component', () => {
  let comp: AuditLogEntryDetail;
  let fixture: ComponentFixture<AuditLogEntryDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./audit-log-entry-detail').then(m => m.AuditLogEntryDetail),
              resolve: { auditLogEntry: () => of({ id: 27321 }) },
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
    fixture = TestBed.createComponent(AuditLogEntryDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load auditLogEntry on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AuditLogEntryDetail);

      // THEN
      expect(instance.auditLogEntry()).toEqual(expect.objectContaining({ id: 27321 }));
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
