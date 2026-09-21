import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPayoutSchedule } from '../payout-schedule.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../payout-schedule.test-samples';

import { PayoutScheduleService } from './payout-schedule.service';

const requireRestSample: IPayoutSchedule = {
  ...sampleWithRequiredData,
};

describe('PayoutSchedule Service', () => {
  let service: PayoutScheduleService;
  let httpMock: HttpTestingController;
  let expectedResult: IPayoutSchedule | IPayoutSchedule[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PayoutScheduleService);
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

    it('should create a PayoutSchedule', () => {
      const payoutSchedule = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(payoutSchedule).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PayoutSchedule', () => {
      const payoutSchedule = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(payoutSchedule).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PayoutSchedule', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PayoutSchedule', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PayoutSchedule', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPayoutScheduleToCollectionIfMissing', () => {
      it('should add a PayoutSchedule to an empty array', () => {
        const payoutSchedule: IPayoutSchedule = sampleWithRequiredData;
        expectedResult = service.addPayoutScheduleToCollectionIfMissing([], payoutSchedule);
        expect(expectedResult).toEqual([payoutSchedule]);
      });

      it('should not add a PayoutSchedule to an array that contains it', () => {
        const payoutSchedule: IPayoutSchedule = sampleWithRequiredData;
        const payoutScheduleCollection: IPayoutSchedule[] = [
          {
            ...payoutSchedule,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPayoutScheduleToCollectionIfMissing(payoutScheduleCollection, payoutSchedule);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PayoutSchedule to an array that doesn't contain it", () => {
        const payoutSchedule: IPayoutSchedule = sampleWithRequiredData;
        const payoutScheduleCollection: IPayoutSchedule[] = [sampleWithPartialData];
        expectedResult = service.addPayoutScheduleToCollectionIfMissing(payoutScheduleCollection, payoutSchedule);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(payoutSchedule);
      });

      it('should add only unique PayoutSchedule to an array', () => {
        const payoutScheduleArray: IPayoutSchedule[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const payoutScheduleCollection: IPayoutSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addPayoutScheduleToCollectionIfMissing(payoutScheduleCollection, ...payoutScheduleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const payoutSchedule: IPayoutSchedule = sampleWithRequiredData;
        const payoutSchedule2: IPayoutSchedule = sampleWithPartialData;
        expectedResult = service.addPayoutScheduleToCollectionIfMissing([], payoutSchedule, payoutSchedule2);
        expect(expectedResult).toEqual([payoutSchedule, payoutSchedule2]);
      });

      it('should accept null and undefined values', () => {
        const payoutSchedule: IPayoutSchedule = sampleWithRequiredData;
        expectedResult = service.addPayoutScheduleToCollectionIfMissing([], null, payoutSchedule, undefined);
        expect(expectedResult).toEqual([payoutSchedule]);
      });

      it('should return initial array if no PayoutSchedule is added', () => {
        const payoutScheduleCollection: IPayoutSchedule[] = [sampleWithRequiredData];
        expectedResult = service.addPayoutScheduleToCollectionIfMissing(payoutScheduleCollection, undefined, null);
        expect(expectedResult).toEqual(payoutScheduleCollection);
      });
    });

    describe('comparePayoutSchedule', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePayoutSchedule(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4713 };
        const entity2 = null;

        const compareResult1 = service.comparePayoutSchedule(entity1, entity2);
        const compareResult2 = service.comparePayoutSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4713 };
        const entity2 = { id: 2981 };

        const compareResult1 = service.comparePayoutSchedule(entity1, entity2);
        const compareResult2 = service.comparePayoutSchedule(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 4713 };
        const entity2 = { id: 4713 };

        const compareResult1 = service.comparePayoutSchedule(entity1, entity2);
        const compareResult2 = service.comparePayoutSchedule(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
