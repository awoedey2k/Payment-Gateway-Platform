import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ISettlementBatch } from '../settlement-batch.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../settlement-batch.test-samples';

import { RestSettlementBatch, SettlementBatchService } from './settlement-batch.service';

const requireRestSample: RestSettlementBatch = {
  ...sampleWithRequiredData,
  scheduledAt: sampleWithRequiredData.scheduledAt?.toJSON(),
  completedAt: sampleWithRequiredData.completedAt?.toJSON(),
};

describe('SettlementBatch Service', () => {
  let service: SettlementBatchService;
  let httpMock: HttpTestingController;
  let expectedResult: ISettlementBatch | ISettlementBatch[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SettlementBatchService);
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

    it('should create a SettlementBatch', () => {
      const settlementBatch = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(settlementBatch).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SettlementBatch', () => {
      const settlementBatch = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(settlementBatch).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SettlementBatch', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SettlementBatch', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SettlementBatch', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addSettlementBatchToCollectionIfMissing', () => {
      it('should add a SettlementBatch to an empty array', () => {
        const settlementBatch: ISettlementBatch = sampleWithRequiredData;
        expectedResult = service.addSettlementBatchToCollectionIfMissing([], settlementBatch);
        expect(expectedResult).toEqual([settlementBatch]);
      });

      it('should not add a SettlementBatch to an array that contains it', () => {
        const settlementBatch: ISettlementBatch = sampleWithRequiredData;
        const settlementBatchCollection: ISettlementBatch[] = [
          {
            ...settlementBatch,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSettlementBatchToCollectionIfMissing(settlementBatchCollection, settlementBatch);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SettlementBatch to an array that doesn't contain it", () => {
        const settlementBatch: ISettlementBatch = sampleWithRequiredData;
        const settlementBatchCollection: ISettlementBatch[] = [sampleWithPartialData];
        expectedResult = service.addSettlementBatchToCollectionIfMissing(settlementBatchCollection, settlementBatch);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(settlementBatch);
      });

      it('should add only unique SettlementBatch to an array', () => {
        const settlementBatchArray: ISettlementBatch[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const settlementBatchCollection: ISettlementBatch[] = [sampleWithRequiredData];
        expectedResult = service.addSettlementBatchToCollectionIfMissing(settlementBatchCollection, ...settlementBatchArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const settlementBatch: ISettlementBatch = sampleWithRequiredData;
        const settlementBatch2: ISettlementBatch = sampleWithPartialData;
        expectedResult = service.addSettlementBatchToCollectionIfMissing([], settlementBatch, settlementBatch2);
        expect(expectedResult).toEqual([settlementBatch, settlementBatch2]);
      });

      it('should accept null and undefined values', () => {
        const settlementBatch: ISettlementBatch = sampleWithRequiredData;
        expectedResult = service.addSettlementBatchToCollectionIfMissing([], null, settlementBatch, undefined);
        expect(expectedResult).toEqual([settlementBatch]);
      });

      it('should return initial array if no SettlementBatch is added', () => {
        const settlementBatchCollection: ISettlementBatch[] = [sampleWithRequiredData];
        expectedResult = service.addSettlementBatchToCollectionIfMissing(settlementBatchCollection, undefined, null);
        expect(expectedResult).toEqual(settlementBatchCollection);
      });
    });

    describe('compareSettlementBatch', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSettlementBatch(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6598 };
        const entity2 = null;

        const compareResult1 = service.compareSettlementBatch(entity1, entity2);
        const compareResult2 = service.compareSettlementBatch(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6598 };
        const entity2 = { id: 30023 };

        const compareResult1 = service.compareSettlementBatch(entity1, entity2);
        const compareResult2 = service.compareSettlementBatch(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 6598 };
        const entity2 = { id: 6598 };

        const compareResult1 = service.compareSettlementBatch(entity1, entity2);
        const compareResult2 = service.compareSettlementBatch(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
