import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPaymentMethod } from '../payment-method.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../payment-method.test-samples';

import { PaymentMethodService } from './payment-method.service';

const requireRestSample: IPaymentMethod = {
  ...sampleWithRequiredData,
};

describe('PaymentMethod Service', () => {
  let service: PaymentMethodService;
  let httpMock: HttpTestingController;
  let expectedResult: IPaymentMethod | IPaymentMethod[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PaymentMethodService);
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

    it('should create a PaymentMethod', () => {
      const paymentMethod = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(paymentMethod).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PaymentMethod', () => {
      const paymentMethod = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(paymentMethod).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PaymentMethod', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PaymentMethod', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PaymentMethod', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPaymentMethodToCollectionIfMissing', () => {
      it('should add a PaymentMethod to an empty array', () => {
        const paymentMethod: IPaymentMethod = sampleWithRequiredData;
        expectedResult = service.addPaymentMethodToCollectionIfMissing([], paymentMethod);
        expect(expectedResult).toEqual([paymentMethod]);
      });

      it('should not add a PaymentMethod to an array that contains it', () => {
        const paymentMethod: IPaymentMethod = sampleWithRequiredData;
        const paymentMethodCollection: IPaymentMethod[] = [
          {
            ...paymentMethod,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPaymentMethodToCollectionIfMissing(paymentMethodCollection, paymentMethod);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PaymentMethod to an array that doesn't contain it", () => {
        const paymentMethod: IPaymentMethod = sampleWithRequiredData;
        const paymentMethodCollection: IPaymentMethod[] = [sampleWithPartialData];
        expectedResult = service.addPaymentMethodToCollectionIfMissing(paymentMethodCollection, paymentMethod);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paymentMethod);
      });

      it('should add only unique PaymentMethod to an array', () => {
        const paymentMethodArray: IPaymentMethod[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const paymentMethodCollection: IPaymentMethod[] = [sampleWithRequiredData];
        expectedResult = service.addPaymentMethodToCollectionIfMissing(paymentMethodCollection, ...paymentMethodArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const paymentMethod: IPaymentMethod = sampleWithRequiredData;
        const paymentMethod2: IPaymentMethod = sampleWithPartialData;
        expectedResult = service.addPaymentMethodToCollectionIfMissing([], paymentMethod, paymentMethod2);
        expect(expectedResult).toEqual([paymentMethod, paymentMethod2]);
      });

      it('should accept null and undefined values', () => {
        const paymentMethod: IPaymentMethod = sampleWithRequiredData;
        expectedResult = service.addPaymentMethodToCollectionIfMissing([], null, paymentMethod, undefined);
        expect(expectedResult).toEqual([paymentMethod]);
      });

      it('should return initial array if no PaymentMethod is added', () => {
        const paymentMethodCollection: IPaymentMethod[] = [sampleWithRequiredData];
        expectedResult = service.addPaymentMethodToCollectionIfMissing(paymentMethodCollection, undefined, null);
        expect(expectedResult).toEqual(paymentMethodCollection);
      });
    });

    describe('comparePaymentMethod', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePaymentMethod(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25086 };
        const entity2 = null;

        const compareResult1 = service.comparePaymentMethod(entity1, entity2);
        const compareResult2 = service.comparePaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25086 };
        const entity2 = { id: 19925 };

        const compareResult1 = service.comparePaymentMethod(entity1, entity2);
        const compareResult2 = service.comparePaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 25086 };
        const entity2 = { id: 25086 };

        const compareResult1 = service.comparePaymentMethod(entity1, entity2);
        const compareResult2 = service.comparePaymentMethod(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
