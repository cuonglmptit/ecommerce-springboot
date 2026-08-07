package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.entity.ProductMedia;

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
    public static ProductMediaCreateResponseDTO fromEntity(ProductMedia media){
        if (media == null) return null;

        return new ProductMediaCreateResponseDTO(
                media.getId(),
                media.getMedia() != null ? media.getMedia().getUrl() : null,
                media.isThumbnail(),
                media.getSortOrder()
        );
    }
}