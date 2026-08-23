package com.cuonglm.ecommerce.backend.media.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.media.dto.external.ConfirmUploadRequest;
import com.cuonglm.ecommerce.backend.media.dto.external.MediaResponse;
import com.cuonglm.ecommerce.backend.media.dto.external.PrepareUploadRequest;
import com.cuonglm.ecommerce.backend.media.dto.external.PrepareUploadResponse;
import com.cuonglm.ecommerce.backend.media.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * MediaController – Controller cho các API liên quan đến Media.
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * 1. Bước chuẩn bị Upload (Kiểm tra trùng Checksum & Ký chữ ký số Cloudinary).
     */
    @PostMapping("/prepare-upload")
    public ResponseEntity<ApiResponse<PrepareUploadResponse>> prepareUpload(
            @Valid @RequestBody PrepareUploadRequest request
    ) {
        PrepareUploadResponse response = mediaService.prepareDirectUpload(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 2. Bước xác nhận sau khi Client đã upload thẳng lên Cloudinary thành công.
     */
    @PostMapping("/confirm-upload")
    public ResponseEntity<ApiResponse<MediaResponse>> confirmUpload(
            @Valid @RequestBody ConfirmUploadRequest request
    ) {
        MediaResponse response = mediaService.confirmDirectUpload(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Đăng ký media thành công", response));
    }

    /**
     * 3. Fallback: Upload thông qua Backend.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaResponse>> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        MediaResponse response = mediaService.uploadProductMedia(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tải file lên thành công", response));
    }
}
