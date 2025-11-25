package com.cuonglm.ecommerce.backend.media.dto.external;

import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;

import java.util.UUID;

/**
 * MediaCreateResponseDTO – Response cho việc tạo media.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public record MediaCreateResponseDTO(
        UUID id,
        String url,
        String alt,
        String title,
        String externalId,
        MediaFormat format
) {
}
