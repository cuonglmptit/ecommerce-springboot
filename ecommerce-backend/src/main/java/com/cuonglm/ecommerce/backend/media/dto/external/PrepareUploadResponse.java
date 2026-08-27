package com.cuonglm.ecommerce.backend.media.dto.external;

import java.util.Map;
import java.util.UUID;

/**
 * PrepareUploadResponse
 *
 * @author cuonglmptit
 * @since Sunday, 23 August 2026
 */
public record PrepareUploadResponse(
        boolean alreadyExists,      // true nếu ảnh đã có trong hệ thống
        UUID mediaId,               // Có giá trị khi alreadyExists = true
        String url,                 // Có giá trị khi alreadyExists = true

        // Các trường phục vụ upload trực tiếp lên Cloudinary khi alreadyExists = false
        String uploadUrl,
        String apiKey,
        Long timestamp,
        String signature,
        String folder,
        String publicId,
        String uploadToken,
        Map<String, Object> extraParams
) {
    public static PrepareUploadResponse existing(UUID mediaId, String url) {
        return new PrepareUploadResponse(true, mediaId, url, null, null, null, null, null, null, null, null);
    }

    public static PrepareUploadResponse directUpload(String uploadUrl, String apiKey, Long timestamp,
                                                     String signature, String folder, String publicId, String uploadToken) {
        return new PrepareUploadResponse(false, null, null, uploadUrl, apiKey, timestamp, signature, folder, publicId, uploadToken, null);
    }
}