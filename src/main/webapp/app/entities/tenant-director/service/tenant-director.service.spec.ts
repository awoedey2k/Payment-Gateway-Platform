import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config';
import { ITenantDirector } from '../tenant-director.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tenant-director.test-samples';

import { RestTenantDirector, TenantDirectorService } from './tenant-director.service';

const requireRestSample: RestTenantDirector = {
  ...sampleWithRequiredData,
  dateOfBirth: sampleWithRequiredData.dateOfBirth?.format(DATE_FORMAT),
};

describe('TenantDirector Service', () => {
  let service: TenantDirectorService;
  let httpMock: HttpTestingController;
  let expectedResult: ITenantDirector | ITenantDirector[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TenantDirectorService);
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

    it('should create a TenantDirector', () => {
      const tenantDirector = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tenantDirector).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TenantDirector', () => {
      const tenantDirector = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tenantDirector).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TenantDirector', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TenantDirector', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TenantDirector', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTenantDirectorToCollectionIfMissing', () => {
      it('should add a TenantDirector to an empty array', () => {
        const tenantDirector: ITenantDirector = sampleWithRequiredData;
        expectedResult = service.addTenantDirectorToCollectionIfMissing([], tenantDirector);
        expect(expectedResult).toEqual([tenantDirector]);
      });

      it('should not add a TenantDirector to an array that contains it', () => {
        const tenantDirector: ITenantDirector = sampleWithRequiredData;
        const tenantDirectorCollection: ITenantDirector[] = [
          {
            ...tenantDirector,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTenantDirectorToCollectionIfMissing(tenantDirectorCollection, tenantDirector);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TenantDirector to an array that doesn't contain it", () => {
        const tenantDirector: ITenantDirector = sampleWithRequiredData;
        const tenantDirectorCollection: ITenantDirector[] = [sampleWithPartialData];
        expectedResult = service.addTenantDirectorToCollectionIfMissing(tenantDirectorCollection, tenantDirector);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantDirector);
      });

      it('should add only unique TenantDirector to an array', () => {
        const tenantDirectorArray: ITenantDirector[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tenantDirectorCollection: ITenantDirector[] = [sampleWithRequiredData];
        expectedResult = service.addTenantDirectorToCollectionIfMissing(tenantDirectorCollection, ...tenantDirectorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenantDirector: ITenantDirector = sampleWithRequiredData;
        const tenantDirector2: ITenantDirector = sampleWithPartialData;
        expectedResult = service.addTenantDirectorToCollectionIfMissing([], tenantDirector, tenantDirector2);
        expect(expectedResult).toEqual([tenantDirector, tenantDirector2]);
      });

      it('should accept null and undefined values', () => {
        const tenantDirector: ITenantDirector = sampleWithRequiredData;
        expectedResult = service.addTenantDirectorToCollectionIfMissing([], null, tenantDirector, undefined);
        expect(expectedResult).toEqual([tenantDirector]);
      });

      it('should return initial array if no TenantDirector is added', () => {
        const tenantDirectorCollection: ITenantDirector[] = [sampleWithRequiredData];
        expectedResult = service.addTenantDirectorToCollectionIfMissing(tenantDirectorCollection, undefined, null);
        expect(expectedResult).toEqual(tenantDirectorCollection);
      });
    });

    describe('compareTenantDirector', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTenantDirector(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7225 };
        const entity2 = null;

        const compareResult1 = service.compareTenantDirector(entity1, entity2);
        const compareResult2 = service.compareTenantDirector(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7225 };
        const entity2 = { id: 25906 };

        const compareResult1 = service.compareTenantDirector(entity1, entity2);
        const compareResult2 = service.compareTenantDirector(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 7225 };
        const entity2 = { id: 7225 };

        const compareResult1 = service.compareTenantDirector(entity1, entity2);
        const compareResult2 = service.compareTenantDirector(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
