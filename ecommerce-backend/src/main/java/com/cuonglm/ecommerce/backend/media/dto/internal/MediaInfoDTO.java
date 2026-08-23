package com.cuonglm.ecommerce.backend.media.dto.internal;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.media.projection.MediaInfoView;

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
        MediaScope scope,
        BasicStatus status,
        Long uploaderId
) {
    public static MediaInfoDTO fromView(MediaInfoView view) {
        if (view == null) return null;
        return new MediaInfoDTO(
                view.getId(),
                view.getUrl(),
                view.getType(),
                view.getScope(),
                view.getStatus(),
                view.getUploaderId()
        );
    }

    public static MediaInfoDTO fromEntity(Media media) {
        if (media == null) return null;
        Long uploaderId = (media.getAudit() != null) ? media.getAudit().getCreatedById() : null;
        return new MediaInfoDTO(
                media.getId(),
                media.getUrl(),
                media.getType(),
                media.getScope(),
                media.getStatus(),
                uploaderId
        );
    }
}