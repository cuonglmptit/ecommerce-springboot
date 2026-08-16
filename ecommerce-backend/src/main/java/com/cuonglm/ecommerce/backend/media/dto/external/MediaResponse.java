package com.cuonglm.ecommerce.backend.media.dto.external;

import com.cuonglm.ecommerce.backend.media.entity.Media;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;

import java.util.UUID;

/**
 * MediaResponse – Response cho việc tạo variantMedia.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public record MediaResponse(
        UUID id,
        String url,
        String alt,
        String title,
        String externalId,
        MediaType type,
        MediaFormat format
) {
    public static MediaResponse fromEntity(Media media) {
        if (media == null) return null;
        return new MediaResponse(
                media.getId(),
                media.getUrl(),
                media.getAlt(),
                media.getTitle(),
                media.getExternalId(),
                media.getType(),
                media.getFormat()
        );
    }
}
