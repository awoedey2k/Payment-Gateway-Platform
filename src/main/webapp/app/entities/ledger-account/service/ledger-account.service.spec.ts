import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILedgerAccount } from '../ledger-account.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ledger-account.test-samples';

import { LedgerAccountService } from './ledger-account.service';

const requireRestSample: ILedgerAccount = {
  ...sampleWithRequiredData,
};

describe('LedgerAccount Service', () => {
  let service: LedgerAccountService;
  let httpMock: HttpTestingController;
  let expectedResult: ILedgerAccount | ILedgerAccount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LedgerAccountService);
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

    it('should create a LedgerAccount', () => {
      const ledgerAccount = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ledgerAccount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LedgerAccount', () => {
      const ledgerAccount = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ledgerAccount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LedgerAccount', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LedgerAccount', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LedgerAccount', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addLedgerAccountToCollectionIfMissing', () => {
      it('should add a LedgerAccount to an empty array', () => {
        const ledgerAccount: ILedgerAccount = sampleWithRequiredData;
        expectedResult = service.addLedgerAccountToCollectionIfMissing([], ledgerAccount);
        expect(expectedResult).toEqual([ledgerAccount]);
      });

      it('should not add a LedgerAccount to an array that contains it', () => {
        const ledgerAccount: ILedgerAccount = sampleWithRequiredData;
        const ledgerAccountCollection: ILedgerAccount[] = [
          {
            ...ledgerAccount,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLedgerAccountToCollectionIfMissing(ledgerAccountCollection, ledgerAccount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LedgerAccount to an array that doesn't contain it", () => {
        const ledgerAccount: ILedgerAccount = sampleWithRequiredData;
        const ledgerAccountCollection: ILedgerAccount[] = [sampleWithPartialData];
        expectedResult = service.addLedgerAccountToCollectionIfMissing(ledgerAccountCollection, ledgerAccount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ledgerAccount);
      });

      it('should add only unique LedgerAccount to an array', () => {
        const ledgerAccountArray: ILedgerAccount[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ledgerAccountCollection: ILedgerAccount[] = [sampleWithRequiredData];
        expectedResult = service.addLedgerAccountToCollectionIfMissing(ledgerAccountCollection, ...ledgerAccountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ledgerAccount: ILedgerAccount = sampleWithRequiredData;
        const ledgerAccount2: ILedgerAccount = sampleWithPartialData;
        expectedResult = service.addLedgerAccountToCollectionIfMissing([], ledgerAccount, ledgerAccount2);
        expect(expectedResult).toEqual([ledgerAccount, ledgerAccount2]);
      });

      it('should accept null and undefined values', () => {
        const ledgerAccount: ILedgerAccount = sampleWithRequiredData;
        expectedResult = service.addLedgerAccountToCollectionIfMissing([], null, ledgerAccount, undefined);
        expect(expectedResult).toEqual([ledgerAccount]);
      });

      it('should return initial array if no LedgerAccount is added', () => {
        const ledgerAccountCollection: ILedgerAccount[] = [sampleWithRequiredData];
        expectedResult = service.addLedgerAccountToCollectionIfMissing(ledgerAccountCollection, undefined, null);
        expect(expectedResult).toEqual(ledgerAccountCollection);
      });
    });

    describe('compareLedgerAccount', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLedgerAccount(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3298 };
        const entity2 = null;

        const compareResult1 = service.compareLedgerAccount(entity1, entity2);
        const compareResult2 = service.compareLedgerAccount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3298 };
        const entity2 = { id: 29910 };

        const compareResult1 = service.compareLedgerAccount(entity1, entity2);
        const compareResult2 = service.compareLedgerAccount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 3298 };
        const entity2 = { id: 3298 };

        const compareResult1 = service.compareLedgerAccount(entity1, entity2);
        const compareResult2 = service.compareLedgerAccount(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
