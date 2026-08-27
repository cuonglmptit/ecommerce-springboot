export type MediaScope = 'PUBLIC' | 'PRIVATE';

export interface PrepareUploadRequest {
  checksum: string;
  fileName: string;
  fileSize: number;
  contentType: string;
  scope?: MediaScope;
}

export interface PrepareUploadResponse {
  alreadyExists: boolean;
  mediaId?: string | null;
  url?: string | null;
  uploadUrl?: string | null;
  apiKey?: string | null;
  timestamp?: number | null;
  signature?: string | null;
  folder?: string | null;
  publicId?: string | null;
  uploadToken?: string | null;
}

export interface ConfirmUploadRequest {
  checksum: string;
  url: string;
  externalId: string;
  format: string;
  size: number;
  version: number;
  signature: string;
  uploadToken: string;
  timestamp: number;
  originalFilename?: string | null;
  alt?: string | null;
  scope?: MediaScope;
}

export interface MediaItemResponse {
  id: string; // UUID Media ID trong Database
  url: string;
  alt?: string | null;
}
