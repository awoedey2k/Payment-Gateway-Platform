import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IJournalLine } from '../journal-line.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../journal-line.test-samples';

import { JournalLineService } from './journal-line.service';

const requireRestSample: IJournalLine = {
  ...sampleWithRequiredData,
};

describe('JournalLine Service', () => {
  let service: JournalLineService;
  let httpMock: HttpTestingController;
  let expectedResult: IJournalLine | IJournalLine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(JournalLineService);
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

    it('should create a JournalLine', () => {
      const journalLine = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(journalLine).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a JournalLine', () => {
      const journalLine = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(journalLine).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a JournalLine', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of JournalLine', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a JournalLine', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addJournalLineToCollectionIfMissing', () => {
      it('should add a JournalLine to an empty array', () => {
        const journalLine: IJournalLine = sampleWithRequiredData;
        expectedResult = service.addJournalLineToCollectionIfMissing([], journalLine);
        expect(expectedResult).toEqual([journalLine]);
      });

      it('should not add a JournalLine to an array that contains it', () => {
        const journalLine: IJournalLine = sampleWithRequiredData;
        const journalLineCollection: IJournalLine[] = [
          {
            ...journalLine,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJournalLineToCollectionIfMissing(journalLineCollection, journalLine);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a JournalLine to an array that doesn't contain it", () => {
        const journalLine: IJournalLine = sampleWithRequiredData;
        const journalLineCollection: IJournalLine[] = [sampleWithPartialData];
        expectedResult = service.addJournalLineToCollectionIfMissing(journalLineCollection, journalLine);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(journalLine);
      });

      it('should add only unique JournalLine to an array', () => {
        const journalLineArray: IJournalLine[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const journalLineCollection: IJournalLine[] = [sampleWithRequiredData];
        expectedResult = service.addJournalLineToCollectionIfMissing(journalLineCollection, ...journalLineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const journalLine: IJournalLine = sampleWithRequiredData;
        const journalLine2: IJournalLine = sampleWithPartialData;
        expectedResult = service.addJournalLineToCollectionIfMissing([], journalLine, journalLine2);
        expect(expectedResult).toEqual([journalLine, journalLine2]);
      });

      it('should accept null and undefined values', () => {
        const journalLine: IJournalLine = sampleWithRequiredData;
        expectedResult = service.addJournalLineToCollectionIfMissing([], null, journalLine, undefined);
        expect(expectedResult).toEqual([journalLine]);
      });

      it('should return initial array if no JournalLine is added', () => {
        const journalLineCollection: IJournalLine[] = [sampleWithRequiredData];
        expectedResult = service.addJournalLineToCollectionIfMissing(journalLineCollection, undefined, null);
        expect(expectedResult).toEqual(journalLineCollection);
      });
    });

    describe('compareJournalLine', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJournalLine(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13943 };
        const entity2 = null;

        const compareResult1 = service.compareJournalLine(entity1, entity2);
        const compareResult2 = service.compareJournalLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13943 };
        const entity2 = { id: 32513 };

        const compareResult1 = service.compareJournalLine(entity1, entity2);
        const compareResult2 = service.compareJournalLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 13943 };
        const entity2 = { id: 13943 };

        const compareResult1 = service.compareJournalLine(entity1, entity2);
        const compareResult2 = service.compareJournalLine(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
