import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICorporateTenant } from '../corporate-tenant.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../corporate-tenant.test-samples';

import { CorporateTenantService, RestCorporateTenant } from './corporate-tenant.service';

const requireRestSample: RestCorporateTenant = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  activatedAt: sampleWithRequiredData.activatedAt?.toJSON(),
};

describe('CorporateTenant Service', () => {
  let service: CorporateTenantService;
  let httpMock: HttpTestingController;
  let expectedResult: ICorporateTenant | ICorporateTenant[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CorporateTenantService);
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

    it('should create a CorporateTenant', () => {
      const corporateTenant = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(corporateTenant).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CorporateTenant', () => {
      const corporateTenant = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(corporateTenant).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CorporateTenant', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CorporateTenant', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CorporateTenant', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addCorporateTenantToCollectionIfMissing', () => {
      it('should add a CorporateTenant to an empty array', () => {
        const corporateTenant: ICorporateTenant = sampleWithRequiredData;
        expectedResult = service.addCorporateTenantToCollectionIfMissing([], corporateTenant);
        expect(expectedResult).toEqual([corporateTenant]);
      });

      it('should not add a CorporateTenant to an array that contains it', () => {
        const corporateTenant: ICorporateTenant = sampleWithRequiredData;
        const corporateTenantCollection: ICorporateTenant[] = [
          {
            ...corporateTenant,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCorporateTenantToCollectionIfMissing(corporateTenantCollection, corporateTenant);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CorporateTenant to an array that doesn't contain it", () => {
        const corporateTenant: ICorporateTenant = sampleWithRequiredData;
        const corporateTenantCollection: ICorporateTenant[] = [sampleWithPartialData];
        expectedResult = service.addCorporateTenantToCollectionIfMissing(corporateTenantCollection, corporateTenant);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(corporateTenant);
      });

      it('should add only unique CorporateTenant to an array', () => {
        const corporateTenantArray: ICorporateTenant[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const corporateTenantCollection: ICorporateTenant[] = [sampleWithRequiredData];
        expectedResult = service.addCorporateTenantToCollectionIfMissing(corporateTenantCollection, ...corporateTenantArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const corporateTenant: ICorporateTenant = sampleWithRequiredData;
        const corporateTenant2: ICorporateTenant = sampleWithPartialData;
        expectedResult = service.addCorporateTenantToCollectionIfMissing([], corporateTenant, corporateTenant2);
        expect(expectedResult).toEqual([corporateTenant, corporateTenant2]);
      });

      it('should accept null and undefined values', () => {
        const corporateTenant: ICorporateTenant = sampleWithRequiredData;
        expectedResult = service.addCorporateTenantToCollectionIfMissing([], null, corporateTenant, undefined);
        expect(expectedResult).toEqual([corporateTenant]);
      });

      it('should return initial array if no CorporateTenant is added', () => {
        const corporateTenantCollection: ICorporateTenant[] = [sampleWithRequiredData];
        expectedResult = service.addCorporateTenantToCollectionIfMissing(corporateTenantCollection, undefined, null);
        expect(expectedResult).toEqual(corporateTenantCollection);
      });
    });

    describe('compareCorporateTenant', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCorporateTenant(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10961 };
        const entity2 = null;

        const compareResult1 = service.compareCorporateTenant(entity1, entity2);
        const compareResult2 = service.compareCorporateTenant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10961 };
        const entity2 = { id: 28703 };

        const compareResult1 = service.compareCorporateTenant(entity1, entity2);
        const compareResult2 = service.compareCorporateTenant(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 10961 };
        const entity2 = { id: 10961 };

        const compareResult1 = service.compareCorporateTenant(entity1, entity2);
        const compareResult2 = service.compareCorporateTenant(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
