import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITenantFeeConfig } from '../tenant-fee-config.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../tenant-fee-config.test-samples';

import { TenantFeeConfigService } from './tenant-fee-config.service';

const requireRestSample: ITenantFeeConfig = {
  ...sampleWithRequiredData,
};

describe('TenantFeeConfig Service', () => {
  let service: TenantFeeConfigService;
  let httpMock: HttpTestingController;
  let expectedResult: ITenantFeeConfig | ITenantFeeConfig[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TenantFeeConfigService);
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

    it('should create a TenantFeeConfig', () => {
      const tenantFeeConfig = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tenantFeeConfig).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TenantFeeConfig', () => {
      const tenantFeeConfig = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tenantFeeConfig).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TenantFeeConfig', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TenantFeeConfig', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TenantFeeConfig', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTenantFeeConfigToCollectionIfMissing', () => {
      it('should add a TenantFeeConfig to an empty array', () => {
        const tenantFeeConfig: ITenantFeeConfig = sampleWithRequiredData;
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing([], tenantFeeConfig);
        expect(expectedResult).toEqual([tenantFeeConfig]);
      });

      it('should not add a TenantFeeConfig to an array that contains it', () => {
        const tenantFeeConfig: ITenantFeeConfig = sampleWithRequiredData;
        const tenantFeeConfigCollection: ITenantFeeConfig[] = [
          {
            ...tenantFeeConfig,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing(tenantFeeConfigCollection, tenantFeeConfig);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TenantFeeConfig to an array that doesn't contain it", () => {
        const tenantFeeConfig: ITenantFeeConfig = sampleWithRequiredData;
        const tenantFeeConfigCollection: ITenantFeeConfig[] = [sampleWithPartialData];
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing(tenantFeeConfigCollection, tenantFeeConfig);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantFeeConfig);
      });

      it('should add only unique TenantFeeConfig to an array', () => {
        const tenantFeeConfigArray: ITenantFeeConfig[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tenantFeeConfigCollection: ITenantFeeConfig[] = [sampleWithRequiredData];
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing(tenantFeeConfigCollection, ...tenantFeeConfigArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenantFeeConfig: ITenantFeeConfig = sampleWithRequiredData;
        const tenantFeeConfig2: ITenantFeeConfig = sampleWithPartialData;
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing([], tenantFeeConfig, tenantFeeConfig2);
        expect(expectedResult).toEqual([tenantFeeConfig, tenantFeeConfig2]);
      });

      it('should accept null and undefined values', () => {
        const tenantFeeConfig: ITenantFeeConfig = sampleWithRequiredData;
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing([], null, tenantFeeConfig, undefined);
        expect(expectedResult).toEqual([tenantFeeConfig]);
      });

      it('should return initial array if no TenantFeeConfig is added', () => {
        const tenantFeeConfigCollection: ITenantFeeConfig[] = [sampleWithRequiredData];
        expectedResult = service.addTenantFeeConfigToCollectionIfMissing(tenantFeeConfigCollection, undefined, null);
        expect(expectedResult).toEqual(tenantFeeConfigCollection);
      });
    });

    describe('compareTenantFeeConfig', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTenantFeeConfig(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 22814 };
        const entity2 = null;

        const compareResult1 = service.compareTenantFeeConfig(entity1, entity2);
        const compareResult2 = service.compareTenantFeeConfig(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 22814 };
        const entity2 = { id: 24087 };

        const compareResult1 = service.compareTenantFeeConfig(entity1, entity2);
        const compareResult2 = service.compareTenantFeeConfig(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 22814 };
        const entity2 = { id: 22814 };

        const compareResult1 = service.compareTenantFeeConfig(entity1, entity2);
        const compareResult2 = service.compareTenantFeeConfig(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
