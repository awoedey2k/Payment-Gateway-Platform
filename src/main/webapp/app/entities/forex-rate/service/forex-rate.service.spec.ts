import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IForexRate } from '../forex-rate.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../forex-rate.test-samples';

import { ForexRateService, RestForexRate } from './forex-rate.service';

const requireRestSample: RestForexRate = {
  ...sampleWithRequiredData,
  lockedAt: sampleWithRequiredData.lockedAt?.toJSON(),
  expiresAt: sampleWithRequiredData.expiresAt?.toJSON(),
};

describe('ForexRate Service', () => {
  let service: ForexRateService;
  let httpMock: HttpTestingController;
  let expectedResult: IForexRate | IForexRate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ForexRateService);
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

    it('should create a ForexRate', () => {
      const forexRate = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(forexRate).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ForexRate', () => {
      const forexRate = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(forexRate).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ForexRate', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ForexRate', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ForexRate', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addForexRateToCollectionIfMissing', () => {
      it('should add a ForexRate to an empty array', () => {
        const forexRate: IForexRate = sampleWithRequiredData;
        expectedResult = service.addForexRateToCollectionIfMissing([], forexRate);
        expect(expectedResult).toEqual([forexRate]);
      });

      it('should not add a ForexRate to an array that contains it', () => {
        const forexRate: IForexRate = sampleWithRequiredData;
        const forexRateCollection: IForexRate[] = [
          {
            ...forexRate,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addForexRateToCollectionIfMissing(forexRateCollection, forexRate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ForexRate to an array that doesn't contain it", () => {
        const forexRate: IForexRate = sampleWithRequiredData;
        const forexRateCollection: IForexRate[] = [sampleWithPartialData];
        expectedResult = service.addForexRateToCollectionIfMissing(forexRateCollection, forexRate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(forexRate);
      });

      it('should add only unique ForexRate to an array', () => {
        const forexRateArray: IForexRate[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const forexRateCollection: IForexRate[] = [sampleWithRequiredData];
        expectedResult = service.addForexRateToCollectionIfMissing(forexRateCollection, ...forexRateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const forexRate: IForexRate = sampleWithRequiredData;
        const forexRate2: IForexRate = sampleWithPartialData;
        expectedResult = service.addForexRateToCollectionIfMissing([], forexRate, forexRate2);
        expect(expectedResult).toEqual([forexRate, forexRate2]);
      });

      it('should accept null and undefined values', () => {
        const forexRate: IForexRate = sampleWithRequiredData;
        expectedResult = service.addForexRateToCollectionIfMissing([], null, forexRate, undefined);
        expect(expectedResult).toEqual([forexRate]);
      });

      it('should return initial array if no ForexRate is added', () => {
        const forexRateCollection: IForexRate[] = [sampleWithRequiredData];
        expectedResult = service.addForexRateToCollectionIfMissing(forexRateCollection, undefined, null);
        expect(expectedResult).toEqual(forexRateCollection);
      });
    });

    describe('compareForexRate', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareForexRate(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29998 };
        const entity2 = null;

        const compareResult1 = service.compareForexRate(entity1, entity2);
        const compareResult2 = service.compareForexRate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29998 };
        const entity2 = { id: 22722 };

        const compareResult1 = service.compareForexRate(entity1, entity2);
        const compareResult2 = service.compareForexRate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 29998 };
        const entity2 = { id: 29998 };

        const compareResult1 = service.compareForexRate(entity1, entity2);
        const compareResult2 = service.compareForexRate(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
