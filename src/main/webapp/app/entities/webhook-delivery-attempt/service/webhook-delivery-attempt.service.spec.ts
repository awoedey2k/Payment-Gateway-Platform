import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IWebhookDeliveryAttempt } from '../webhook-delivery-attempt.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../webhook-delivery-attempt.test-samples';

import { RestWebhookDeliveryAttempt, WebhookDeliveryAttemptService } from './webhook-delivery-attempt.service';

const requireRestSample: RestWebhookDeliveryAttempt = {
  ...sampleWithRequiredData,
  attemptedAt: sampleWithRequiredData.attemptedAt?.toJSON(),
};

describe('WebhookDeliveryAttempt Service', () => {
  let service: WebhookDeliveryAttemptService;
  let httpMock: HttpTestingController;
  let expectedResult: IWebhookDeliveryAttempt | IWebhookDeliveryAttempt[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(WebhookDeliveryAttemptService);
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

    it('should create a WebhookDeliveryAttempt', () => {
      const webhookDeliveryAttempt = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(webhookDeliveryAttempt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WebhookDeliveryAttempt', () => {
      const webhookDeliveryAttempt = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(webhookDeliveryAttempt).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WebhookDeliveryAttempt', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WebhookDeliveryAttempt', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WebhookDeliveryAttempt', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addWebhookDeliveryAttemptToCollectionIfMissing', () => {
      it('should add a WebhookDeliveryAttempt to an empty array', () => {
        const webhookDeliveryAttempt: IWebhookDeliveryAttempt = sampleWithRequiredData;
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing([], webhookDeliveryAttempt);
        expect(expectedResult).toEqual([webhookDeliveryAttempt]);
      });

      it('should not add a WebhookDeliveryAttempt to an array that contains it', () => {
        const webhookDeliveryAttempt: IWebhookDeliveryAttempt = sampleWithRequiredData;
        const webhookDeliveryAttemptCollection: IWebhookDeliveryAttempt[] = [
          {
            ...webhookDeliveryAttempt,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing(webhookDeliveryAttemptCollection, webhookDeliveryAttempt);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WebhookDeliveryAttempt to an array that doesn't contain it", () => {
        const webhookDeliveryAttempt: IWebhookDeliveryAttempt = sampleWithRequiredData;
        const webhookDeliveryAttemptCollection: IWebhookDeliveryAttempt[] = [sampleWithPartialData];
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing(webhookDeliveryAttemptCollection, webhookDeliveryAttempt);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(webhookDeliveryAttempt);
      });

      it('should add only unique WebhookDeliveryAttempt to an array', () => {
        const webhookDeliveryAttemptArray: IWebhookDeliveryAttempt[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const webhookDeliveryAttemptCollection: IWebhookDeliveryAttempt[] = [sampleWithRequiredData];
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing(
          webhookDeliveryAttemptCollection,
          ...webhookDeliveryAttemptArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const webhookDeliveryAttempt: IWebhookDeliveryAttempt = sampleWithRequiredData;
        const webhookDeliveryAttempt2: IWebhookDeliveryAttempt = sampleWithPartialData;
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing([], webhookDeliveryAttempt, webhookDeliveryAttempt2);
        expect(expectedResult).toEqual([webhookDeliveryAttempt, webhookDeliveryAttempt2]);
      });

      it('should accept null and undefined values', () => {
        const webhookDeliveryAttempt: IWebhookDeliveryAttempt = sampleWithRequiredData;
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing([], null, webhookDeliveryAttempt, undefined);
        expect(expectedResult).toEqual([webhookDeliveryAttempt]);
      });

      it('should return initial array if no WebhookDeliveryAttempt is added', () => {
        const webhookDeliveryAttemptCollection: IWebhookDeliveryAttempt[] = [sampleWithRequiredData];
        expectedResult = service.addWebhookDeliveryAttemptToCollectionIfMissing(webhookDeliveryAttemptCollection, undefined, null);
        expect(expectedResult).toEqual(webhookDeliveryAttemptCollection);
      });
    });

    describe('compareWebhookDeliveryAttempt', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWebhookDeliveryAttempt(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3237 };
        const entity2 = null;

        const compareResult1 = service.compareWebhookDeliveryAttempt(entity1, entity2);
        const compareResult2 = service.compareWebhookDeliveryAttempt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3237 };
        const entity2 = { id: 17935 };

        const compareResult1 = service.compareWebhookDeliveryAttempt(entity1, entity2);
        const compareResult2 = service.compareWebhookDeliveryAttempt(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 3237 };
        const entity2 = { id: 3237 };

        const compareResult1 = service.compareWebhookDeliveryAttempt(entity1, entity2);
        const compareResult2 = service.compareWebhookDeliveryAttempt(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
