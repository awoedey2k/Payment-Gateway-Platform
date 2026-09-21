import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAmlCheck } from '../aml-check.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../aml-check.test-samples';

import { AmlCheckService, RestAmlCheck } from './aml-check.service';

const requireRestSample: RestAmlCheck = {
  ...sampleWithRequiredData,
  checkedAt: sampleWithRequiredData.checkedAt?.toJSON(),
};

describe('AmlCheck Service', () => {
  let service: AmlCheckService;
  let httpMock: HttpTestingController;
  let expectedResult: IAmlCheck | IAmlCheck[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AmlCheckService);
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

    it('should create a AmlCheck', () => {
      const amlCheck = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(amlCheck).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AmlCheck', () => {
      const amlCheck = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(amlCheck).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AmlCheck', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AmlCheck', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AmlCheck', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAmlCheckToCollectionIfMissing', () => {
      it('should add a AmlCheck to an empty array', () => {
        const amlCheck: IAmlCheck = sampleWithRequiredData;
        expectedResult = service.addAmlCheckToCollectionIfMissing([], amlCheck);
        expect(expectedResult).toEqual([amlCheck]);
      });

      it('should not add a AmlCheck to an array that contains it', () => {
        const amlCheck: IAmlCheck = sampleWithRequiredData;
        const amlCheckCollection: IAmlCheck[] = [
          {
            ...amlCheck,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAmlCheckToCollectionIfMissing(amlCheckCollection, amlCheck);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AmlCheck to an array that doesn't contain it", () => {
        const amlCheck: IAmlCheck = sampleWithRequiredData;
        const amlCheckCollection: IAmlCheck[] = [sampleWithPartialData];
        expectedResult = service.addAmlCheckToCollectionIfMissing(amlCheckCollection, amlCheck);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(amlCheck);
      });

      it('should add only unique AmlCheck to an array', () => {
        const amlCheckArray: IAmlCheck[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const amlCheckCollection: IAmlCheck[] = [sampleWithRequiredData];
        expectedResult = service.addAmlCheckToCollectionIfMissing(amlCheckCollection, ...amlCheckArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const amlCheck: IAmlCheck = sampleWithRequiredData;
        const amlCheck2: IAmlCheck = sampleWithPartialData;
        expectedResult = service.addAmlCheckToCollectionIfMissing([], amlCheck, amlCheck2);
        expect(expectedResult).toEqual([amlCheck, amlCheck2]);
      });

      it('should accept null and undefined values', () => {
        const amlCheck: IAmlCheck = sampleWithRequiredData;
        expectedResult = service.addAmlCheckToCollectionIfMissing([], null, amlCheck, undefined);
        expect(expectedResult).toEqual([amlCheck]);
      });

      it('should return initial array if no AmlCheck is added', () => {
        const amlCheckCollection: IAmlCheck[] = [sampleWithRequiredData];
        expectedResult = service.addAmlCheckToCollectionIfMissing(amlCheckCollection, undefined, null);
        expect(expectedResult).toEqual(amlCheckCollection);
      });
    });

    describe('compareAmlCheck', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAmlCheck(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28734 };
        const entity2 = null;

        const compareResult1 = service.compareAmlCheck(entity1, entity2);
        const compareResult2 = service.compareAmlCheck(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28734 };
        const entity2 = { id: 14754 };

        const compareResult1 = service.compareAmlCheck(entity1, entity2);
        const compareResult2 = service.compareAmlCheck(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 28734 };
        const entity2 = { id: 28734 };

        const compareResult1 = service.compareAmlCheck(entity1, entity2);
        const compareResult2 = service.compareAmlCheck(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
