import { beforeEach, describe, expect, it, vi } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IDispute } from 'app/entities/dispute/dispute.model';
import { DisputeService } from 'app/entities/dispute/service/dispute.service';
import { IDisputeEvidence } from '../dispute-evidence.model';
import { DisputeEvidenceService } from '../service/dispute-evidence.service';

import { DisputeEvidenceFormService } from './dispute-evidence-form.service';
import { DisputeEvidenceUpdate } from './dispute-evidence-update';

describe('DisputeEvidence Management Update Component', () => {
  let comp: DisputeEvidenceUpdate;
  let fixture: ComponentFixture<DisputeEvidenceUpdate>;
  let activatedRoute: ActivatedRoute;
  let disputeEvidenceFormService: DisputeEvidenceFormService;
  let disputeEvidenceService: DisputeEvidenceService;
  let disputeService: DisputeService;

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

    fixture = TestBed.createComponent(DisputeEvidenceUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    disputeEvidenceFormService = TestBed.inject(DisputeEvidenceFormService);
    disputeEvidenceService = TestBed.inject(DisputeEvidenceService);
    disputeService = TestBed.inject(DisputeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Dispute query and add missing value', () => {
      const disputeEvidence: IDisputeEvidence = { id: 28628 };
      const dispute: IDispute = { id: 30381 };
      disputeEvidence.dispute = dispute;

      const disputeCollection: IDispute[] = [{ id: 30381 }];
      vi.spyOn(disputeService, 'query').mockReturnValue(of(new HttpResponse({ body: disputeCollection })));
      const additionalDisputes = [dispute];
      const expectedCollection: IDispute[] = [...additionalDisputes, ...disputeCollection];
      vi.spyOn(disputeService, 'addDisputeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ disputeEvidence });
      comp.ngOnInit();

      expect(disputeService.query).toHaveBeenCalled();
      expect(disputeService.addDisputeToCollectionIfMissing).toHaveBeenCalledWith(
        disputeCollection,
        ...additionalDisputes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.disputesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const disputeEvidence: IDisputeEvidence = { id: 28628 };
      const dispute: IDispute = { id: 30381 };
      disputeEvidence.dispute = dispute;

      activatedRoute.data = of({ disputeEvidence });
      comp.ngOnInit();

      expect(comp.disputesSharedCollection()).toContainEqual(dispute);
      expect(comp.disputeEvidence).toEqual(disputeEvidence);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDisputeEvidence>();
      const disputeEvidence = { id: 6499 };
      vi.spyOn(disputeEvidenceFormService, 'getDisputeEvidence').mockReturnValue(disputeEvidence);
      vi.spyOn(disputeEvidenceService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disputeEvidence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(disputeEvidence);
      saveSubject.complete();

      // THEN
      expect(disputeEvidenceFormService.getDisputeEvidence).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(disputeEvidenceService.update).toHaveBeenCalledWith(expect.objectContaining(disputeEvidence));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDisputeEvidence>();
      const disputeEvidence = { id: 6499 };
      vi.spyOn(disputeEvidenceFormService, 'getDisputeEvidence').mockReturnValue({ id: null });
      vi.spyOn(disputeEvidenceService, 'create').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disputeEvidence: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(disputeEvidence);
      saveSubject.complete();

      // THEN
      expect(disputeEvidenceFormService.getDisputeEvidence).toHaveBeenCalled();
      expect(disputeEvidenceService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDisputeEvidence>();
      const disputeEvidence = { id: 6499 };
      vi.spyOn(disputeEvidenceService, 'update').mockReturnValue(saveSubject);
      vi.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disputeEvidence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(disputeEvidenceService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDispute', () => {
      it('should forward to disputeService', () => {
        const entity = { id: 30381 };
        const entity2 = { id: 5064 };
        vi.spyOn(disputeService, 'compareDispute');
        comp.compareDispute(entity, entity2);
        expect(disputeService.compareDispute).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
