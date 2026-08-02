package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.media.dto.external.MediaCreateResponseDTO;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoView;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * MediaService – Interface mô tả các phương thức thao tác với {@link Media}.
 *
 * <p>
 * Xử lý nghiệp vụ upload variantMedia, lưu thông tin vào database và liên kết với người dùng.
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 24 November 2025
 */
public interface MediaService {
    /**
     * Upload variantMedia file lên Cloud thông qua Backend an toàn.
     *
     * @param file File cần upload
     * @return DTO chứa thông tin variantMedia đã tạo.
     */
    MediaCreateResponseDTO uploadProductMedia(MultipartFile file);

    Optional<MediaInfoDTO> findMediaInfoById(UUID id);

    Media getMediaReference(UUID mediaId);
}
