import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDisputeEvidence } from '../dispute-evidence.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dispute-evidence.test-samples';

import { DisputeEvidenceService, RestDisputeEvidence } from './dispute-evidence.service';

const requireRestSample: RestDisputeEvidence = {
  ...sampleWithRequiredData,
  uploadedAt: sampleWithRequiredData.uploadedAt?.toJSON(),
};

describe('DisputeEvidence Service', () => {
  let service: DisputeEvidenceService;
  let httpMock: HttpTestingController;
  let expectedResult: IDisputeEvidence | IDisputeEvidence[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DisputeEvidenceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DisputeEvidence', () => {
      const disputeEvidence = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(disputeEvidence).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DisputeEvidence', () => {
      const disputeEvidence = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(disputeEvidence).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DisputeEvidence', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DisputeEvidence', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DisputeEvidence', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addDisputeEvidenceToCollectionIfMissing', () => {
      it('should add a DisputeEvidence to an empty array', () => {
        const disputeEvidence: IDisputeEvidence = sampleWithRequiredData;
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing([], disputeEvidence);
        expect(expectedResult).toEqual([disputeEvidence]);
      });

      it('should not add a DisputeEvidence to an array that contains it', () => {
        const disputeEvidence: IDisputeEvidence = sampleWithRequiredData;
        const disputeEvidenceCollection: IDisputeEvidence[] = [
          {
            ...disputeEvidence,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing(disputeEvidenceCollection, disputeEvidence);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DisputeEvidence to an array that doesn't contain it", () => {
        const disputeEvidence: IDisputeEvidence = sampleWithRequiredData;
        const disputeEvidenceCollection: IDisputeEvidence[] = [sampleWithPartialData];
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing(disputeEvidenceCollection, disputeEvidence);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(disputeEvidence);
      });

      it('should add only unique DisputeEvidence to an array', () => {
        const disputeEvidenceArray: IDisputeEvidence[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const disputeEvidenceCollection: IDisputeEvidence[] = [sampleWithRequiredData];
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing(disputeEvidenceCollection, ...disputeEvidenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const disputeEvidence: IDisputeEvidence = sampleWithRequiredData;
        const disputeEvidence2: IDisputeEvidence = sampleWithPartialData;
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing([], disputeEvidence, disputeEvidence2);
        expect(expectedResult).toEqual([disputeEvidence, disputeEvidence2]);
      });

      it('should accept null and undefined values', () => {
        const disputeEvidence: IDisputeEvidence = sampleWithRequiredData;
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing([], null, disputeEvidence, undefined);
        expect(expectedResult).toEqual([disputeEvidence]);
      });

      it('should return initial array if no DisputeEvidence is added', () => {
        const disputeEvidenceCollection: IDisputeEvidence[] = [sampleWithRequiredData];
        expectedResult = service.addDisputeEvidenceToCollectionIfMissing(disputeEvidenceCollection, undefined, null);
        expect(expectedResult).toEqual(disputeEvidenceCollection);
      });
    });

    describe('compareDisputeEvidence', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDisputeEvidence(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6499 };
        const entity2 = null;

        const compareResult1 = service.compareDisputeEvidence(entity1, entity2);
        const compareResult2 = service.compareDisputeEvidence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6499 };
        const entity2 = { id: 28628 };

        const compareResult1 = service.compareDisputeEvidence(entity1, entity2);
        const compareResult2 = service.compareDisputeEvidence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 6499 };
        const entity2 = { id: 6499 };

        const compareResult1 = service.compareDisputeEvidence(entity1, entity2);
        const compareResult2 = service.compareDisputeEvidence(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
