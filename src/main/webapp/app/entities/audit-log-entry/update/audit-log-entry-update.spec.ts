import { beforeEach, describe, expect, it, vi } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IAuditLogEntry } from '../audit-log-entry.model';
import { AuditLogEntryService } from '../service/audit-log-entry.service';

import { AuditLogEntryFormService } from './audit-log-entry-form.service';
import { AuditLogEntryUpdate } from './audit-log-entry-update';

describe('AuditLogEntry Management Update Component', () => {
  let comp: AuditLogEntryUpdate;
  let fixture: ComponentFixture<AuditLogEntryUpdate>;
  let activatedRoute: ActivatedRoute;
  let auditLogEntryFormService: AuditLogEntryFormService;
  let auditLogEntryService: AuditLogEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(AuditLogEntryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    auditLogEntryFormService = TestBed.inject(AuditLogEntryFormService);
    auditLogEntryService = TestBed.inject(AuditLogEntryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const auditLogEntry: IAuditLogEntry = { id: 19436 };

      activatedRoute.data = of({ auditLogEntry });
      comp.ngOnInit();

      expect(comp.auditLogEntry).toEqual(auditLogEntry);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLogEntry>();
      const auditLogEntry = { id: 27321 };
      vi.spyOn(auditLogEntryFormService, 'getAuditLogEntry').mockReturnValue(auditLogEntry);
      vi.spyOn(auditLogEntryService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLogEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(auditLogEntry);
      saveSubject.complete();

      // THEN
      expect(auditLogEntryFormService.getAuditLogEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(auditLogEntryService.update).toHaveBeenCalledWith(expect.objectContaining(auditLogEntry));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLogEntry>();
      const auditLogEntry = { id: 27321 };
      vi.spyOn(auditLogEntryFormService, 'getAuditLogEntry').mockReturnValue({ id: null });
      vi.spyOn(auditLogEntryService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLogEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(auditLogEntry);
      saveSubject.complete();

      // THEN
      expect(auditLogEntryFormService.getAuditLogEntry).toHaveBeenCalled();
      expect(auditLogEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAuditLogEntry>();
      const auditLogEntry = { id: 27321 };
      vi.spyOn(auditLogEntryService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLogEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(auditLogEntryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
