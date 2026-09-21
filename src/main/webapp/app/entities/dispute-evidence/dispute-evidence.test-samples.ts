import dayjs from 'dayjs/esm';

import { IDisputeEvidence, NewDisputeEvidence } from './dispute-evidence.model';

export const sampleWithRequiredData: IDisputeEvidence = {
  id: 12251,
  evidenceType: 'REFUND_POLICY',
  fileName: 'deeply statement',
  fileUrl: 'thin optimistically jealously',
  fileSizeBytes: 27692,
  mimeType: 'zowie meh',
  sha256Checksum: 'everlasting conversation championship',
  uploadedAt: dayjs('2026-09-20T17:58'),
};

export const sampleWithPartialData: IDisputeEvidence = {
  id: 25633,
  evidenceType: 'PROOF_OF_DELIVERY',
  fileName: 'fax ingratiate hubris',
  fileUrl: 'oh delight battle',
  fileSizeBytes: 5557,
  mimeType: 'reasonable awful whoa',
  sha256Checksum: 'than quaintly',
  uploadedAt: dayjs('2026-09-20T18:27'),
};

export const sampleWithFullData: IDisputeEvidence = {
  id: 11767,
  evidenceType: 'CUSTOMER_COMMUNICATION',
  fileName: 'alert',
  fileUrl: 'teriyaki when',
  fileSizeBytes: 31953,
  mimeType: 'instead',
  sha256Checksum: 'lest overcook',
  uploadedAt: dayjs('2026-09-21T11:07'),
};

export const sampleWithNewData: NewDisputeEvidence = {
  evidenceType: 'PROOF_OF_DELIVERY',
  fileName: 'whup',
  fileUrl: 'colorful finally',
  fileSizeBytes: 22406,
  mimeType: 'clamp revere warming',
  sha256Checksum: 'colon recede',
  uploadedAt: dayjs('2026-09-21T13:02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
