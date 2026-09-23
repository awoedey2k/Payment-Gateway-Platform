import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IWebhookSubscription } from '../webhook-subscription.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../webhook-subscription.test-samples';

import { WebhookSubscriptionService } from './webhook-subscription.service';

const requireRestSample: IWebhookSubscription = {
  ...sampleWithRequiredData,
};

describe('WebhookSubscription Service', () => {
  let service: WebhookSubscriptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IWebhookSubscription | IWebhookSubscription[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(WebhookSubscriptionService);
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

    it('should create a WebhookSubscription', () => {
      const webhookSubscription = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(webhookSubscription).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WebhookSubscription', () => {
      const webhookSubscription = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(webhookSubscription).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WebhookSubscription', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WebhookSubscription', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WebhookSubscription', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addWebhookSubscriptionToCollectionIfMissing', () => {
      it('should add a WebhookSubscription to an empty array', () => {
        const webhookSubscription: IWebhookSubscription = sampleWithRequiredData;
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing([], webhookSubscription);
        expect(expectedResult).toEqual([webhookSubscription]);
      });

      it('should not add a WebhookSubscription to an array that contains it', () => {
        const webhookSubscription: IWebhookSubscription = sampleWithRequiredData;
        const webhookSubscriptionCollection: IWebhookSubscription[] = [
          {
            ...webhookSubscription,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing(webhookSubscriptionCollection, webhookSubscription);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WebhookSubscription to an array that doesn't contain it", () => {
        const webhookSubscription: IWebhookSubscription = sampleWithRequiredData;
        const webhookSubscriptionCollection: IWebhookSubscription[] = [sampleWithPartialData];
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing(webhookSubscriptionCollection, webhookSubscription);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(webhookSubscription);
      });

      it('should add only unique WebhookSubscription to an array', () => {
        const webhookSubscriptionArray: IWebhookSubscription[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const webhookSubscriptionCollection: IWebhookSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing(webhookSubscriptionCollection, ...webhookSubscriptionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const webhookSubscription: IWebhookSubscription = sampleWithRequiredData;
        const webhookSubscription2: IWebhookSubscription = sampleWithPartialData;
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing([], webhookSubscription, webhookSubscription2);
        expect(expectedResult).toEqual([webhookSubscription, webhookSubscription2]);
      });

      it('should accept null and undefined values', () => {
        const webhookSubscription: IWebhookSubscription = sampleWithRequiredData;
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing([], null, webhookSubscription, undefined);
        expect(expectedResult).toEqual([webhookSubscription]);
      });

      it('should return initial array if no WebhookSubscription is added', () => {
        const webhookSubscriptionCollection: IWebhookSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addWebhookSubscriptionToCollectionIfMissing(webhookSubscriptionCollection, undefined, null);
        expect(expectedResult).toEqual(webhookSubscriptionCollection);
      });
    });

    describe('compareWebhookSubscription', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWebhookSubscription(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28283 };
        const entity2 = null;

        const compareResult1 = service.compareWebhookSubscription(entity1, entity2);
        const compareResult2 = service.compareWebhookSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28283 };
        const entity2 = { id: 24725 };

        const compareResult1 = service.compareWebhookSubscription(entity1, entity2);
        const compareResult2 = service.compareWebhookSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 28283 };
        const entity2 = { id: 28283 };

        const compareResult1 = service.compareWebhookSubscription(entity1, entity2);
        const compareResult2 = service.compareWebhookSubscription(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
