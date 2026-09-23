import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITenantDomain } from '../tenant-domain.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tenant-domain.test-samples';

import { TenantDomainService } from './tenant-domain.service';

const requireRestSample: ITenantDomain = {
  ...sampleWithRequiredData,
};

describe('TenantDomain Service', () => {
  let service: TenantDomainService;
  let httpMock: HttpTestingController;
  let expectedResult: ITenantDomain | ITenantDomain[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TenantDomainService);
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

    it('should create a TenantDomain', () => {
      const tenantDomain = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tenantDomain).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TenantDomain', () => {
      const tenantDomain = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tenantDomain).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TenantDomain', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TenantDomain', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TenantDomain', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTenantDomainToCollectionIfMissing', () => {
      it('should add a TenantDomain to an empty array', () => {
        const tenantDomain: ITenantDomain = sampleWithRequiredData;
        expectedResult = service.addTenantDomainToCollectionIfMissing([], tenantDomain);
        expect(expectedResult).toEqual([tenantDomain]);
      });

      it('should not add a TenantDomain to an array that contains it', () => {
        const tenantDomain: ITenantDomain = sampleWithRequiredData;
        const tenantDomainCollection: ITenantDomain[] = [
          {
            ...tenantDomain,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTenantDomainToCollectionIfMissing(tenantDomainCollection, tenantDomain);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TenantDomain to an array that doesn't contain it", () => {
        const tenantDomain: ITenantDomain = sampleWithRequiredData;
        const tenantDomainCollection: ITenantDomain[] = [sampleWithPartialData];
        expectedResult = service.addTenantDomainToCollectionIfMissing(tenantDomainCollection, tenantDomain);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantDomain);
      });

      it('should add only unique TenantDomain to an array', () => {
        const tenantDomainArray: ITenantDomain[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tenantDomainCollection: ITenantDomain[] = [sampleWithRequiredData];
        expectedResult = service.addTenantDomainToCollectionIfMissing(tenantDomainCollection, ...tenantDomainArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenantDomain: ITenantDomain = sampleWithRequiredData;
        const tenantDomain2: ITenantDomain = sampleWithPartialData;
        expectedResult = service.addTenantDomainToCollectionIfMissing([], tenantDomain, tenantDomain2);
        expect(expectedResult).toEqual([tenantDomain, tenantDomain2]);
      });

      it('should accept null and undefined values', () => {
        const tenantDomain: ITenantDomain = sampleWithRequiredData;
        expectedResult = service.addTenantDomainToCollectionIfMissing([], null, tenantDomain, undefined);
        expect(expectedResult).toEqual([tenantDomain]);
      });

      it('should return initial array if no TenantDomain is added', () => {
        const tenantDomainCollection: ITenantDomain[] = [sampleWithRequiredData];
        expectedResult = service.addTenantDomainToCollectionIfMissing(tenantDomainCollection, undefined, null);
        expect(expectedResult).toEqual(tenantDomainCollection);
      });
    });

    describe('compareTenantDomain', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTenantDomain(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19717 };
        const entity2 = null;

        const compareResult1 = service.compareTenantDomain(entity1, entity2);
        const compareResult2 = service.compareTenantDomain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19717 };
        const entity2 = { id: 5642 };

        const compareResult1 = service.compareTenantDomain(entity1, entity2);
        const compareResult2 = service.compareTenantDomain(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 19717 };
        const entity2 = { id: 19717 };

        const compareResult1 = service.compareTenantDomain(entity1, entity2);
        const compareResult2 = service.compareTenantDomain(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
