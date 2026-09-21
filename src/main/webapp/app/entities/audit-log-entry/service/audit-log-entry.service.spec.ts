import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAuditLogEntry } from '../audit-log-entry.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../audit-log-entry.test-samples';

import { AuditLogEntryService, RestAuditLogEntry } from './audit-log-entry.service';

const requireRestSample: RestAuditLogEntry = {
  ...sampleWithRequiredData,
  recordedAt: sampleWithRequiredData.recordedAt?.toJSON(),
};

describe('AuditLogEntry Service', () => {
  let service: AuditLogEntryService;
  let httpMock: HttpTestingController;
  let expectedResult: IAuditLogEntry | IAuditLogEntry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AuditLogEntryService);
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

    it('should create a AuditLogEntry', () => {
      const auditLogEntry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(auditLogEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AuditLogEntry', () => {
      const auditLogEntry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(auditLogEntry).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AuditLogEntry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AuditLogEntry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AuditLogEntry', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAuditLogEntryToCollectionIfMissing', () => {
      it('should add a AuditLogEntry to an empty array', () => {
        const auditLogEntry: IAuditLogEntry = sampleWithRequiredData;
        expectedResult = service.addAuditLogEntryToCollectionIfMissing([], auditLogEntry);
        expect(expectedResult).toEqual([auditLogEntry]);
      });

      it('should not add a AuditLogEntry to an array that contains it', () => {
        const auditLogEntry: IAuditLogEntry = sampleWithRequiredData;
        const auditLogEntryCollection: IAuditLogEntry[] = [
          {
            ...auditLogEntry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAuditLogEntryToCollectionIfMissing(auditLogEntryCollection, auditLogEntry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AuditLogEntry to an array that doesn't contain it", () => {
        const auditLogEntry: IAuditLogEntry = sampleWithRequiredData;
        const auditLogEntryCollection: IAuditLogEntry[] = [sampleWithPartialData];
        expectedResult = service.addAuditLogEntryToCollectionIfMissing(auditLogEntryCollection, auditLogEntry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(auditLogEntry);
      });

      it('should add only unique AuditLogEntry to an array', () => {
        const auditLogEntryArray: IAuditLogEntry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const auditLogEntryCollection: IAuditLogEntry[] = [sampleWithRequiredData];
        expectedResult = service.addAuditLogEntryToCollectionIfMissing(auditLogEntryCollection, ...auditLogEntryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const auditLogEntry: IAuditLogEntry = sampleWithRequiredData;
        const auditLogEntry2: IAuditLogEntry = sampleWithPartialData;
        expectedResult = service.addAuditLogEntryToCollectionIfMissing([], auditLogEntry, auditLogEntry2);
        expect(expectedResult).toEqual([auditLogEntry, auditLogEntry2]);
      });

      it('should accept null and undefined values', () => {
        const auditLogEntry: IAuditLogEntry = sampleWithRequiredData;
        expectedResult = service.addAuditLogEntryToCollectionIfMissing([], null, auditLogEntry, undefined);
        expect(expectedResult).toEqual([auditLogEntry]);
      });

      it('should return initial array if no AuditLogEntry is added', () => {
        const auditLogEntryCollection: IAuditLogEntry[] = [sampleWithRequiredData];
        expectedResult = service.addAuditLogEntryToCollectionIfMissing(auditLogEntryCollection, undefined, null);
        expect(expectedResult).toEqual(auditLogEntryCollection);
      });
    });

    describe('compareAuditLogEntry', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAuditLogEntry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27321 };
        const entity2 = null;

        const compareResult1 = service.compareAuditLogEntry(entity1, entity2);
        const compareResult2 = service.compareAuditLogEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27321 };
        const entity2 = { id: 19436 };

        const compareResult1 = service.compareAuditLogEntry(entity1, entity2);
        const compareResult2 = service.compareAuditLogEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return true if primaryKey matches', () => {
        const entity1 = { id: 27321 };
        const entity2 = { id: 27321 };

        const compareResult1 = service.compareAuditLogEntry(entity1, entity2);
        const compareResult2 = service.compareAuditLogEntry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
