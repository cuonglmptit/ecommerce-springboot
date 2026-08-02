package com.cuonglm.ecommerce.backend.product.dto.external;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * ProductMediaCreateRequestDTO – Dto cho tạo ProductMedia.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public record ProductMediaCreateRequestDTO(
        @NotNull(message = "Media ID không được để trống")
        UUID mediaId,

        boolean isThumbnail,

        int sortOrder
) {
}
