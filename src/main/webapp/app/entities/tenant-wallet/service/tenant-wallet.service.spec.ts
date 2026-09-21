import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITenantWallet } from '../tenant-wallet.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tenant-wallet.test-samples';

import { TenantWalletService } from './tenant-wallet.service';

const requireRestSample: ITenantWallet = {
  ...sampleWithRequiredData,
};

describe('TenantWallet Service', () => {
  let service: TenantWalletService;
  let httpMock: HttpTestingController;
  let expectedResult: ITenantWallet | ITenantWallet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TenantWalletService);
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

    it('should create a TenantWallet', () => {
      const tenantWallet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tenantWallet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TenantWallet', () => {
      const tenantWallet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tenantWallet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TenantWallet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TenantWallet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TenantWallet', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTenantWalletToCollectionIfMissing', () => {
      it('should add a TenantWallet to an empty array', () => {
        const tenantWallet: ITenantWallet = sampleWithRequiredData;
        expectedResult = service.addTenantWalletToCollectionIfMissing([], tenantWallet);
        expect(expectedResult).toEqual([tenantWallet]);
      });

      it('should not add a TenantWallet to an array that contains it', () => {
        const tenantWallet: ITenantWallet = sampleWithRequiredData;
        const tenantWalletCollection: ITenantWallet[] = [
          {
            ...tenantWallet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTenantWalletToCollectionIfMissing(tenantWalletCollection, tenantWallet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TenantWallet to an array that doesn't contain it", () => {
        const tenantWallet: ITenantWallet = sampleWithRequiredData;
        const tenantWalletCollection: ITenantWallet[] = [sampleWithPartialData];
        expectedResult = service.addTenantWalletToCollectionIfMissing(tenantWalletCollection, tenantWallet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantWallet);
      });

      it('should add only unique TenantWallet to an array', () => {
        const tenantWalletArray: ITenantWallet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tenantWalletCollection: ITenantWallet[] = [sampleWithRequiredData];
        expectedResult = service.addTenantWalletToCollectionIfMissing(tenantWalletCollection, ...tenantWalletArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenantWallet: ITenantWallet = sampleWithRequiredData;
        const tenantWallet2: ITenantWallet = sampleWithPartialData;
        expectedResult = service.addTenantWalletToCollectionIfMissing([], tenantWallet, tenantWallet2);
        expect(expectedResult).toEqual([tenantWallet, tenantWallet2]);
      });

      it('should accept null and undefined values', () => {
        const tenantWallet: ITenantWallet = sampleWithRequiredData;
        expectedResult = service.addTenantWalletToCollectionIfMissing([], null, tenantWallet, undefined);
        expect(expectedResult).toEqual([tenantWallet]);
      });

      it('should return initial array if no TenantWallet is added', () => {
        const tenantWalletCollection: ITenantWallet[] = [sampleWithRequiredData];
        expectedResult = service.addTenantWalletToCollectionIfMissing(tenantWalletCollection, undefined, null);
        expect(expectedResult).toEqual(tenantWalletCollection);
      });
    });

    describe('compareTenantWallet', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTenantWallet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17782 };
        const entity2 = null;

        const compareResult1 = service.compareTenantWallet(entity1, entity2);
        const compareResult2 = service.compareTenantWallet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17782 };
        const entity2 = { id: 21354 };

        const compareResult1 = service.compareTenantWallet(entity1, entity2);
        const compareResult2 = service.compareTenantWallet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 17782 };
        const entity2 = { id: 17782 };

        const compareResult1 = service.compareTenantWallet(entity1, entity2);
        const compareResult2 = service.compareTenantWallet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
