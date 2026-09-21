import dayjs from 'dayjs/esm';

import { IDispute } from 'app/entities/dispute/dispute.model';
import { DisputeEvidenceType } from 'app/entities/enumerations/dispute-evidence-type.model';

export interface IDisputeEvidence {
  id: number;
  evidenceType?: keyof typeof DisputeEvidenceType | null;
  fileName?: string | null;
  fileUrl?: string | null;
  fileSizeBytes?: number | null;
  mimeType?: string | null;
  sha256Checksum?: string | null;
  uploadedAt?: dayjs.Dayjs | null;
  dispute?: Pick<IDispute, 'id'> | null;
}

export type NewDisputeEvidence = Omit<IDisputeEvidence, 'id'> & { id: null };
