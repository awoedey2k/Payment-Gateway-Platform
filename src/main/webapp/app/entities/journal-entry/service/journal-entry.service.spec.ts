import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IJournalEntry } from '../journal-entry.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../journal-entry.test-samples';

import { JournalEntryService, RestJournalEntry } from './journal-entry.service';

const requireRestSample: RestJournalEntry = {
  ...sampleWithRequiredData,
  postedAt: sampleWithRequiredData.postedAt?.toJSON(),
};

describe('JournalEntry Service', () => {
  let service: JournalEntryService;
  let httpMock: HttpTestingController;
  let expectedResult: IJournalEntry | IJournalEntry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(JournalEntryService);
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

    it('should create a JournalEntry', () => {
      const journalEntry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(journalEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a JournalEntry', () => {
      const journalEntry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(journalEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a JournalEntry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of JournalEntry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a JournalEntry', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addJournalEntryToCollectionIfMissing', () => {
      it('should add a JournalEntry to an empty array', () => {
        const journalEntry: IJournalEntry = sampleWithRequiredData;
        expectedResult = service.addJournalEntryToCollectionIfMissing([], journalEntry);
        expect(expectedResult).toEqual([journalEntry]);
      });

      it('should not add a JournalEntry to an array that contains it', () => {
        const journalEntry: IJournalEntry = sampleWithRequiredData;
        const journalEntryCollection: IJournalEntry[] = [
          {
            ...journalEntry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addJournalEntryToCollectionIfMissing(journalEntryCollection, journalEntry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a JournalEntry to an array that doesn't contain it", () => {
        const journalEntry: IJournalEntry = sampleWithRequiredData;
        const journalEntryCollection: IJournalEntry[] = [sampleWithPartialData];
        expectedResult = service.addJournalEntryToCollectionIfMissing(journalEntryCollection, journalEntry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(journalEntry);
      });

      it('should add only unique JournalEntry to an array', () => {
        const journalEntryArray: IJournalEntry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const journalEntryCollection: IJournalEntry[] = [sampleWithRequiredData];
        expectedResult = service.addJournalEntryToCollectionIfMissing(journalEntryCollection, ...journalEntryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const journalEntry: IJournalEntry = sampleWithRequiredData;
        const journalEntry2: IJournalEntry = sampleWithPartialData;
        expectedResult = service.addJournalEntryToCollectionIfMissing([], journalEntry, journalEntry2);
        expect(expectedResult).toEqual([journalEntry, journalEntry2]);
      });

      it('should accept null and undefined values', () => {
        const journalEntry: IJournalEntry = sampleWithRequiredData;
        expectedResult = service.addJournalEntryToCollectionIfMissing([], null, journalEntry, undefined);
        expect(expectedResult).toEqual([journalEntry]);
      });

      it('should return initial array if no JournalEntry is added', () => {
        const journalEntryCollection: IJournalEntry[] = [sampleWithRequiredData];
        expectedResult = service.addJournalEntryToCollectionIfMissing(journalEntryCollection, undefined, null);
        expect(expectedResult).toEqual(journalEntryCollection);
      });
    });

    describe('compareJournalEntry', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareJournalEntry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28031 };
        const entity2 = null;

        const compareResult1 = service.compareJournalEntry(entity1, entity2);
        const compareResult2 = service.compareJournalEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28031 };
        const entity2 = { id: 2017 };

        const compareResult1 = service.compareJournalEntry(entity1, entity2);
        const compareResult2 = service.compareJournalEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 28031 };
        const entity2 = { id: 28031 };

        const compareResult1 = service.compareJournalEntry(entity1, entity2);
        const compareResult2 = service.compareJournalEntry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
