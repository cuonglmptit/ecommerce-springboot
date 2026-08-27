import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { from, map, Observable, of, retry, switchMap, timer } from 'rxjs';
import { ApiResponse } from '@buytamin/shared/models';
import { calculateFileSha256 } from '@buytamin/shared/util';
import {
  ConfirmUploadRequest,
  MediaItemResponse,
  MediaScope,
  PrepareUploadRequest,
  PrepareUploadResponse,
} from './media.models';

@Injectable({
  providedIn: 'root',
})
export class MediaApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/media';

  uploadMediaDirect(
    file: File,
    scope: MediaScope = 'PUBLIC',
    alt?: string,
  ): Observable<MediaItemResponse> {
    return from(calculateFileSha256(file)).pipe(
      switchMap((checksum) => {
        const preparePayload: PrepareUploadRequest = {
          checksum,
          fileName: file.name,
          fileSize: file.size,
          contentType: file.type,
          scope,
        };

        // BƯỚC 1: Bắt tay Backend
        return this.http
          .post<
            ApiResponse<PrepareUploadResponse>
          >(`${this.baseUrl}/prepare-upload`, preparePayload)
          .pipe(
            switchMap((res) => {
              const prep = res.data;

              // Case A: File đã có sẵn -> Hoàn tất ngay lập tức (0s, 0 byte upload)
              if (prep.alreadyExists && prep.mediaId && prep.url) {
                return of({
                  id: prep.mediaId,
                  url: prep.url,
                  alt: alt || file.name,
                });
              }

              // Trích xuất biến cục bộ để TypeScript tự động Type-Narrowing
              const {
                uploadUrl,
                apiKey,
                signature,
                folder,
                publicId,
                uploadToken,
                timestamp,
              } = prep;

              if (
                !uploadUrl ||
                !apiKey ||
                !signature ||
                !uploadToken ||
                timestamp === null ||
                timestamp === undefined
              ) {
                throw new Error(
                  'Máy chủ không cung cấp đủ thông số bảo mật cho phiên tải lên.',
                );
              }

              // BƯỚC 2: Upload trực tiếp sang Cloudinary
              const formData = new FormData();
              formData.append('file', file);
              formData.append('api_key', apiKey);
              formData.append('timestamp', String(timestamp));
              formData.append('signature', signature);
              if (folder) formData.append('folder', folder);
              if (publicId) formData.append('public_id', publicId);

              return this.http.post<any>(uploadUrl, formData).pipe(
                // BƯỚC 3: Xác nhận với Backend (Chuyển tiếp Chữ ký Cloudinary + uploadToken)
                switchMap((cloudRes) => {
                  const confirmPayload: ConfirmUploadRequest = {
                    checksum,
                    url: cloudRes.secure_url,
                    externalId: cloudRes.public_id,
                    format: cloudRes.format,
                    size: cloudRes.bytes,

                    version: cloudRes.version,
                    signature: cloudRes.signature,
                    uploadToken: uploadToken,
                    timestamp: timestamp,

                    originalFilename: file.name,
                    alt: alt || file.name,
                    scope,
                  };

                  return this.http
                    .post<
                      ApiResponse<any>
                    >(`${this.baseUrl}/confirm-upload`, confirmPayload)
                    .pipe(
                      retry({
                        count: 3,
                        delay: (error, retryCount) => {
                          console.warn(
                            `[MediaApi] Thử lại xác nhận upload lần ${retryCount}...`,
                            error,
                          );
                          return timer(2000);
                        },
                      }),
                      map((confirmRes) => ({
                        id: confirmRes.data.id,
                        url: confirmRes.data.url,
                        alt: confirmRes.data.alt,
                      })),
                    );
                }),
              );
            }),
          );
      }),
    );
  }
}
