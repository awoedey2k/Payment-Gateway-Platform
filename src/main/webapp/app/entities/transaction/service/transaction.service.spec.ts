import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITransaction } from '../transaction.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../transaction.test-samples';

import { RestTransaction, TransactionService } from './transaction.service';

const requireRestSample: RestTransaction = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  completedAt: sampleWithRequiredData.completedAt?.toJSON(),
};

describe('Transaction Service', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;
  let expectedResult: ITransaction | ITransaction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TransactionService);
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

    it('should create a Transaction', () => {
      const transaction = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(transaction).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Transaction', () => {
      const transaction = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(transaction).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Transaction', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Transaction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Transaction', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTransactionToCollectionIfMissing', () => {
      it('should add a Transaction to an empty array', () => {
        const transaction: ITransaction = sampleWithRequiredData;
        expectedResult = service.addTransactionToCollectionIfMissing([], transaction);
        expect(expectedResult).toEqual([transaction]);
      });

      it('should not add a Transaction to an array that contains it', () => {
        const transaction: ITransaction = sampleWithRequiredData;
        const transactionCollection: ITransaction[] = [
          {
            ...transaction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Transaction to an array that doesn't contain it", () => {
        const transaction: ITransaction = sampleWithRequiredData;
        const transactionCollection: ITransaction[] = [sampleWithPartialData];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transaction);
      });

      it('should add only unique Transaction to an array', () => {
        const transactionArray: ITransaction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const transactionCollection: ITransaction[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, ...transactionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transaction: ITransaction = sampleWithRequiredData;
        const transaction2: ITransaction = sampleWithPartialData;
        expectedResult = service.addTransactionToCollectionIfMissing([], transaction, transaction2);
        expect(expectedResult).toEqual([transaction, transaction2]);
      });

      it('should accept null and undefined values', () => {
        const transaction: ITransaction = sampleWithRequiredData;
        expectedResult = service.addTransactionToCollectionIfMissing([], null, transaction, undefined);
        expect(expectedResult).toEqual([transaction]);
      });

      it('should return initial array if no Transaction is added', () => {
        const transactionCollection: ITransaction[] = [sampleWithRequiredData];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, undefined, null);
        expect(expectedResult).toEqual(transactionCollection);
      });
    });

    describe('compareTransaction', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTransaction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29476 };
        const entity2 = null;

        const compareResult1 = service.compareTransaction(entity1, entity2);
        const compareResult2 = service.compareTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29476 };
        const entity2 = { id: 15110 };

        const compareResult1 = service.compareTransaction(entity1, entity2);
        const compareResult2 = service.compareTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 29476 };
        const entity2 = { id: 29476 };

        const compareResult1 = service.compareTransaction(entity1, entity2);
        const compareResult2 = service.compareTransaction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
