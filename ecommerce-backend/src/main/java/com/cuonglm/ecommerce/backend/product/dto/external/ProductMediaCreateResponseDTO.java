package com.cuonglm.ecommerce.backend.product.dto.external;

import java.util.UUID;

/**
 * ProductMediaCreateResponseDTO – Dto Response cho ProductMedia.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Thursday, 27 November 2025
 */
public record ProductMediaCreateResponseDTO(
        UUID id,
        String url,
        boolean isThumbnail,
        int sortOrder
) {
}