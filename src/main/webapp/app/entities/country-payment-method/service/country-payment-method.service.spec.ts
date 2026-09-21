import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICountryPaymentMethod } from '../country-payment-method.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../country-payment-method.test-samples';

import { CountryPaymentMethodService } from './country-payment-method.service';

const requireRestSample: ICountryPaymentMethod = {
  ...sampleWithRequiredData,
};

describe('CountryPaymentMethod Service', () => {
  let service: CountryPaymentMethodService;
  let httpMock: HttpTestingController;
  let expectedResult: ICountryPaymentMethod | ICountryPaymentMethod[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CountryPaymentMethodService);
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

    it('should create a CountryPaymentMethod', () => {
      const countryPaymentMethod = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(countryPaymentMethod).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CountryPaymentMethod', () => {
      const countryPaymentMethod = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(countryPaymentMethod).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CountryPaymentMethod', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CountryPaymentMethod', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CountryPaymentMethod', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addCountryPaymentMethodToCollectionIfMissing', () => {
      it('should add a CountryPaymentMethod to an empty array', () => {
        const countryPaymentMethod: ICountryPaymentMethod = sampleWithRequiredData;
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing([], countryPaymentMethod);
        expect(expectedResult).toEqual([countryPaymentMethod]);
      });

      it('should not add a CountryPaymentMethod to an array that contains it', () => {
        const countryPaymentMethod: ICountryPaymentMethod = sampleWithRequiredData;
        const countryPaymentMethodCollection: ICountryPaymentMethod[] = [
          {
            ...countryPaymentMethod,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing(countryPaymentMethodCollection, countryPaymentMethod);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CountryPaymentMethod to an array that doesn't contain it", () => {
        const countryPaymentMethod: ICountryPaymentMethod = sampleWithRequiredData;
        const countryPaymentMethodCollection: ICountryPaymentMethod[] = [sampleWithPartialData];
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing(countryPaymentMethodCollection, countryPaymentMethod);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(countryPaymentMethod);
      });

      it('should add only unique CountryPaymentMethod to an array', () => {
        const countryPaymentMethodArray: ICountryPaymentMethod[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const countryPaymentMethodCollection: ICountryPaymentMethod[] = [sampleWithRequiredData];
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing(countryPaymentMethodCollection, ...countryPaymentMethodArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const countryPaymentMethod: ICountryPaymentMethod = sampleWithRequiredData;
        const countryPaymentMethod2: ICountryPaymentMethod = sampleWithPartialData;
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing([], countryPaymentMethod, countryPaymentMethod2);
        expect(expectedResult).toEqual([countryPaymentMethod, countryPaymentMethod2]);
      });

      it('should accept null and undefined values', () => {
        const countryPaymentMethod: ICountryPaymentMethod = sampleWithRequiredData;
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing([], null, countryPaymentMethod, undefined);
        expect(expectedResult).toEqual([countryPaymentMethod]);
      });

      it('should return initial array if no CountryPaymentMethod is added', () => {
        const countryPaymentMethodCollection: ICountryPaymentMethod[] = [sampleWithRequiredData];
        expectedResult = service.addCountryPaymentMethodToCollectionIfMissing(countryPaymentMethodCollection, undefined, null);
        expect(expectedResult).toEqual(countryPaymentMethodCollection);
      });
    });

    describe('compareCountryPaymentMethod', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCountryPaymentMethod(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19642 };
        const entity2 = null;

        const compareResult1 = service.compareCountryPaymentMethod(entity1, entity2);
        const compareResult2 = service.compareCountryPaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19642 };
        const entity2 = { id: 27921 };

        const compareResult1 = service.compareCountryPaymentMethod(entity1, entity2);
        const compareResult2 = service.compareCountryPaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 19642 };
        const entity2 = { id: 19642 };

        const compareResult1 = service.compareCountryPaymentMethod(entity1, entity2);
        const compareResult2 = service.compareCountryPaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
