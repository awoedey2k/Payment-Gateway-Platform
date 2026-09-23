import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IApiKey } from '../api-key.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../api-key.test-samples';

import { ApiKeyService, RestApiKey } from './api-key.service';

const requireRestSample: RestApiKey = {
  ...sampleWithRequiredData,
  issuedAt: sampleWithRequiredData.issuedAt?.toJSON(),
  revokedAt: sampleWithRequiredData.revokedAt?.toJSON(),
  graceExpiresAt: sampleWithRequiredData.graceExpiresAt?.toJSON(),
};

describe('ApiKey Service', () => {
  let service: ApiKeyService;
  let httpMock: HttpTestingController;
  let expectedResult: IApiKey | IApiKey[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ApiKeyService);
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

    it('should create a ApiKey', () => {
      const apiKey = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(apiKey).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ApiKey', () => {
      const apiKey = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(apiKey).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ApiKey', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ApiKey', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ApiKey', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addApiKeyToCollectionIfMissing', () => {
      it('should add a ApiKey to an empty array', () => {
        const apiKey: IApiKey = sampleWithRequiredData;
        expectedResult = service.addApiKeyToCollectionIfMissing([], apiKey);
        expect(expectedResult).toEqual([apiKey]);
      });

      it('should not add a ApiKey to an array that contains it', () => {
        const apiKey: IApiKey = sampleWithRequiredData;
        const apiKeyCollection: IApiKey[] = [
          {
            ...apiKey,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, apiKey);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ApiKey to an array that doesn't contain it", () => {
        const apiKey: IApiKey = sampleWithRequiredData;
        const apiKeyCollection: IApiKey[] = [sampleWithPartialData];
        expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, apiKey);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(apiKey);
      });

      it('should add only unique ApiKey to an array', () => {
        const apiKeyArray: IApiKey[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const apiKeyCollection: IApiKey[] = [sampleWithRequiredData];
        expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, ...apiKeyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const apiKey: IApiKey = sampleWithRequiredData;
        const apiKey2: IApiKey = sampleWithPartialData;
        expectedResult = service.addApiKeyToCollectionIfMissing([], apiKey, apiKey2);
        expect(expectedResult).toEqual([apiKey, apiKey2]);
      });

      it('should accept null and undefined values', () => {
        const apiKey: IApiKey = sampleWithRequiredData;
        expectedResult = service.addApiKeyToCollectionIfMissing([], null, apiKey, undefined);
        expect(expectedResult).toEqual([apiKey]);
      });

      it('should return initial array if no ApiKey is added', () => {
        const apiKeyCollection: IApiKey[] = [sampleWithRequiredData];
        expectedResult = service.addApiKeyToCollectionIfMissing(apiKeyCollection, undefined, null);
        expect(expectedResult).toEqual(apiKeyCollection);
      });
    });

    describe('compareApiKey', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareApiKey(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16763 };
        const entity2 = null;

        const compareResult1 = service.compareApiKey(entity1, entity2);
        const compareResult2 = service.compareApiKey(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16763 };
        const entity2 = { id: 24872 };

        const compareResult1 = service.compareApiKey(entity1, entity2);
        const compareResult2 = service.compareApiKey(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 16763 };
        const entity2 = { id: 16763 };

        const compareResult1 = service.compareApiKey(entity1, entity2);
        const compareResult2 = service.compareApiKey(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
