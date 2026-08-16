package com.cuonglm.ecommerce.backend.media.dto.internal;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;

import java.util.UUID;

/**
 * MediaInfoDTO – Dto về thông tin của Media.
 *
 * @author cuonglmptit
 * @since Monday, 01 December 2025
 */
public record MediaInfoDTO(
        UUID id,
        String url,
        MediaType type,
        BasicStatus status,
        Long uploaderId
) {
    public static MediaInfoDTO fromView(MediaInfoView view) {
        if (view == null) return null;
        Long uploaderId = (view.getUploader() != null) ? view.getUploader().getId() : null;
        return new MediaInfoDTO(
                view.getId(),
                view.getUrl(),
                view.getType(),
                view.getStatus(),
                uploaderId
        );
    }

    public static MediaInfoDTO fromEntity(Media media) {
        if (media == null) return null;
        Long uploaderId = (media.getUploader() != null) ? media.getUploader().getId() : null;
        return new MediaInfoDTO(
                media.getId(),
                media.getUrl(),
                media.getType(),
                media.getStatus(),
                uploaderId
        );
    }
}