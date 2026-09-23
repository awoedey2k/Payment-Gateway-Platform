import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRefund } from '../refund.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../refund.test-samples';

import { RefundService, RestRefund } from './refund.service';

const requireRestSample: RestRefund = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('Refund Service', () => {
  let service: RefundService;
  let httpMock: HttpTestingController;
  let expectedResult: IRefund | IRefund[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RefundService);
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

    it('should create a Refund', () => {
      const refund = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(refund).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Refund', () => {
      const refund = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(refund).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Refund', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Refund', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Refund', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addRefundToCollectionIfMissing', () => {
      it('should add a Refund to an empty array', () => {
        const refund: IRefund = sampleWithRequiredData;
        expectedResult = service.addRefundToCollectionIfMissing([], refund);
        expect(expectedResult).toEqual([refund]);
      });

      it('should not add a Refund to an array that contains it', () => {
        const refund: IRefund = sampleWithRequiredData;
        const refundCollection: IRefund[] = [
          {
            ...refund,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRefundToCollectionIfMissing(refundCollection, refund);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Refund to an array that doesn't contain it", () => {
        const refund: IRefund = sampleWithRequiredData;
        const refundCollection: IRefund[] = [sampleWithPartialData];
        expectedResult = service.addRefundToCollectionIfMissing(refundCollection, refund);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(refund);
      });

      it('should add only unique Refund to an array', () => {
        const refundArray: IRefund[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const refundCollection: IRefund[] = [sampleWithRequiredData];
        expectedResult = service.addRefundToCollectionIfMissing(refundCollection, ...refundArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const refund: IRefund = sampleWithRequiredData;
        const refund2: IRefund = sampleWithPartialData;
        expectedResult = service.addRefundToCollectionIfMissing([], refund, refund2);
        expect(expectedResult).toEqual([refund, refund2]);
      });

      it('should accept null and undefined values', () => {
        const refund: IRefund = sampleWithRequiredData;
        expectedResult = service.addRefundToCollectionIfMissing([], null, refund, undefined);
        expect(expectedResult).toEqual([refund]);
      });

      it('should return initial array if no Refund is added', () => {
        const refundCollection: IRefund[] = [sampleWithRequiredData];
        expectedResult = service.addRefundToCollectionIfMissing(refundCollection, undefined, null);
        expect(expectedResult).toEqual(refundCollection);
      });
    });

    describe('compareRefund', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRefund(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20042 };
        const entity2 = null;

        const compareResult1 = service.compareRefund(entity1, entity2);
        const compareResult2 = service.compareRefund(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20042 };
        const entity2 = { id: 1065 };

        const compareResult1 = service.compareRefund(entity1, entity2);
        const compareResult2 = service.compareRefund(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 20042 };
        const entity2 = { id: 20042 };

        const compareResult1 = service.compareRefund(entity1, entity2);
        const compareResult2 = service.compareRefund(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
