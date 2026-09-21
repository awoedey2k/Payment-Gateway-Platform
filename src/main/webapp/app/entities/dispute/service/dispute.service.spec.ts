import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IDispute } from '../dispute.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dispute.test-samples';

import { DisputeService, RestDispute } from './dispute.service';

const requireRestSample: RestDispute = {
  ...sampleWithRequiredData,
  dueDate: sampleWithRequiredData.dueDate?.toJSON(),
  evidenceSubmittedAt: sampleWithRequiredData.evidenceSubmittedAt?.toJSON(),
  resolvedAt: sampleWithRequiredData.resolvedAt?.toJSON(),
};

describe('Dispute Service', () => {
  let service: DisputeService;
  let httpMock: HttpTestingController;
  let expectedResult: IDispute | IDispute[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DisputeService);
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

    it('should create a Dispute', () => {
      const dispute = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dispute).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dispute', () => {
      const dispute = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dispute).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Dispute', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dispute', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Dispute', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addDisputeToCollectionIfMissing', () => {
      it('should add a Dispute to an empty array', () => {
        const dispute: IDispute = sampleWithRequiredData;
        expectedResult = service.addDisputeToCollectionIfMissing([], dispute);
        expect(expectedResult).toEqual([dispute]);
      });

      it('should not add a Dispute to an array that contains it', () => {
        const dispute: IDispute = sampleWithRequiredData;
        const disputeCollection: IDispute[] = [
          {
            ...dispute,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDisputeToCollectionIfMissing(disputeCollection, dispute);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dispute to an array that doesn't contain it", () => {
        const dispute: IDispute = sampleWithRequiredData;
        const disputeCollection: IDispute[] = [sampleWithPartialData];
        expectedResult = service.addDisputeToCollectionIfMissing(disputeCollection, dispute);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dispute);
      });

      it('should add only unique Dispute to an array', () => {
        const disputeArray: IDispute[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const disputeCollection: IDispute[] = [sampleWithRequiredData];
        expectedResult = service.addDisputeToCollectionIfMissing(disputeCollection, ...disputeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dispute: IDispute = sampleWithRequiredData;
        const dispute2: IDispute = sampleWithPartialData;
        expectedResult = service.addDisputeToCollectionIfMissing([], dispute, dispute2);
        expect(expectedResult).toEqual([dispute, dispute2]);
      });

      it('should accept null and undefined values', () => {
        const dispute: IDispute = sampleWithRequiredData;
        expectedResult = service.addDisputeToCollectionIfMissing([], null, dispute, undefined);
        expect(expectedResult).toEqual([dispute]);
      });

      it('should return initial array if no Dispute is added', () => {
        const disputeCollection: IDispute[] = [sampleWithRequiredData];
        expectedResult = service.addDisputeToCollectionIfMissing(disputeCollection, undefined, null);
        expect(expectedResult).toEqual(disputeCollection);
      });
    });

    describe('compareDispute', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDispute(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 30381 };
        const entity2 = null;

        const compareResult1 = service.compareDispute(entity1, entity2);
        const compareResult2 = service.compareDispute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 30381 };
        const entity2 = { id: 5064 };

        const compareResult1 = service.compareDispute(entity1, entity2);
        const compareResult2 = service.compareDispute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 30381 };
        const entity2 = { id: 30381 };

        const compareResult1 = service.compareDispute(entity1, entity2);
        const compareResult2 = service.compareDispute(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
