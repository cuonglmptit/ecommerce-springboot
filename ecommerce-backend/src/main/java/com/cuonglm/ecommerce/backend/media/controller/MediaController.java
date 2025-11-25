package com.cuonglm.ecommerce.backend.media.controller;

import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.media.dto.external.MediaCreateResponseDTO;
import com.cuonglm.ecommerce.backend.media.service.MediaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaCreateResponseDTO>> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        // Gọi Service để upload lên Cloud và lưu Entity Media
        MediaCreateResponseDTO response = mediaService.uploadProductMedia(file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
