import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRoutingRule } from '../routing-rule.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../routing-rule.test-samples';

import { RoutingRuleService } from './routing-rule.service';

const requireRestSample: IRoutingRule = {
  ...sampleWithRequiredData,
};

describe('RoutingRule Service', () => {
  let service: RoutingRuleService;
  let httpMock: HttpTestingController;
  let expectedResult: IRoutingRule | IRoutingRule[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RoutingRuleService);
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

    it('should create a RoutingRule', () => {
      const routingRule = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(routingRule).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RoutingRule', () => {
      const routingRule = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(routingRule).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RoutingRule', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RoutingRule', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RoutingRule', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addRoutingRuleToCollectionIfMissing', () => {
      it('should add a RoutingRule to an empty array', () => {
        const routingRule: IRoutingRule = sampleWithRequiredData;
        expectedResult = service.addRoutingRuleToCollectionIfMissing([], routingRule);
        expect(expectedResult).toEqual([routingRule]);
      });

      it('should not add a RoutingRule to an array that contains it', () => {
        const routingRule: IRoutingRule = sampleWithRequiredData;
        const routingRuleCollection: IRoutingRule[] = [
          {
            ...routingRule,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRoutingRuleToCollectionIfMissing(routingRuleCollection, routingRule);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RoutingRule to an array that doesn't contain it", () => {
        const routingRule: IRoutingRule = sampleWithRequiredData;
        const routingRuleCollection: IRoutingRule[] = [sampleWithPartialData];
        expectedResult = service.addRoutingRuleToCollectionIfMissing(routingRuleCollection, routingRule);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(routingRule);
      });

      it('should add only unique RoutingRule to an array', () => {
        const routingRuleArray: IRoutingRule[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const routingRuleCollection: IRoutingRule[] = [sampleWithRequiredData];
        expectedResult = service.addRoutingRuleToCollectionIfMissing(routingRuleCollection, ...routingRuleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const routingRule: IRoutingRule = sampleWithRequiredData;
        const routingRule2: IRoutingRule = sampleWithPartialData;
        expectedResult = service.addRoutingRuleToCollectionIfMissing([], routingRule, routingRule2);
        expect(expectedResult).toEqual([routingRule, routingRule2]);
      });

      it('should accept null and undefined values', () => {
        const routingRule: IRoutingRule = sampleWithRequiredData;
        expectedResult = service.addRoutingRuleToCollectionIfMissing([], null, routingRule, undefined);
        expect(expectedResult).toEqual([routingRule]);
      });

      it('should return initial array if no RoutingRule is added', () => {
        const routingRuleCollection: IRoutingRule[] = [sampleWithRequiredData];
        expectedResult = service.addRoutingRuleToCollectionIfMissing(routingRuleCollection, undefined, null);
        expect(expectedResult).toEqual(routingRuleCollection);
      });
    });

    describe('compareRoutingRule', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRoutingRule(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9786 };
        const entity2 = null;

        const compareResult1 = service.compareRoutingRule(entity1, entity2);
        const compareResult2 = service.compareRoutingRule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9786 };
        const entity2 = { id: 776 };

        const compareResult1 = service.compareRoutingRule(entity1, entity2);
        const compareResult2 = service.compareRoutingRule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 9786 };
        const entity2 = { id: 9786 };

        const compareResult1 = service.compareRoutingRule(entity1, entity2);
        const compareResult2 = service.compareRoutingRule(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
