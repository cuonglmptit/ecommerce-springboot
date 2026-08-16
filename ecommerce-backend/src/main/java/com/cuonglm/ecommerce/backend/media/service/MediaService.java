package com.cuonglm.ecommerce.backend.media.service;

import com.cuonglm.ecommerce.backend.media.dto.external.MediaResponse;
import com.cuonglm.ecommerce.backend.media.dto.internal.MediaInfoDTO;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
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
    MediaResponse uploadProductMedia(MultipartFile file);

    /**
     * Tìm thông tin 1 Media theo ID.
     */
    Optional<MediaInfoDTO> findMediaInfoById(UUID id);

    /**
     * Batch query lấy danh sách Media theo danh sách ID (Chống N+1 query).
     */
    List<MediaInfoDTO> findMediaInfoByIds(Collection<UUID> ids);

    /**
     * Lấy JPA Proxy Reference để liên kết entity mà không cần query SELECT.
     */
    Media getMediaReference(UUID mediaId);
}
