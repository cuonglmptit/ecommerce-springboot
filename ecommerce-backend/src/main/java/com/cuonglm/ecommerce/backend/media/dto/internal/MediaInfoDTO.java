package com.cuonglm.ecommerce.backend.media.dto.internal;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
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
        MediaType type,
        BasicStatus status,
        Long uploaderId
) {
}